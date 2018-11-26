package org.keremulutas.mockeyjockey.benchmarks;

import io.smartcat.ranger.BuilderMethods;
import io.smartcat.ranger.ObjectGenerator;
import org.keremulutas.mockeyjockey.MockeyJockey;
import org.keremulutas.mockeyjockey.benchmarks.beans.Platform;
import org.keremulutas.mockeyjockey.benchmarks.beans.User;
import org.keremulutas.mockeyjockey.core.generator.*;
import org.keremulutas.mockeyjockey.utils.Constants;
import org.openjdk.jmh.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static io.smartcat.ranger.BuilderMethods.*;

// http://www.baeldung.com/java-microbenchmark-harness
// http://hg.openjdk.java.net/code-tools/jmh/file/fbe1b55eadf8/jmh-samples/src/main/java/org/openjdk/jmh/samples/
// https://github.com/greenlaw110/java-str-benchmark/blob/master/src/main/java/sample/StringReplaceBenchmark.java
public class BenchmarkRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(BenchmarkRunner.class);

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

    @State(Scope.Thread)
    public static class MapFill {

        int count = 1_000_000;

        HashMap<Integer, String> hashMap = new HashMap<>();
        LinkedHashMap<Integer, String> linkedHashMap = new LinkedHashMap<>();

        @Benchmark
        @Measurement(iterations = 5, time = 1)
        @Fork(value = 1, warmups = 1)
        @Warmup(iterations = 3)
        @OutputTimeUnit(TimeUnit.SECONDS)
        public void regularHashMap() {
            for (int i = 0; i < count; i++) {
                hashMap.put(i, String.valueOf(i));
            }
        }

        @Benchmark
        @Measurement(iterations = 5, time = 1)
        @Fork(value = 1, warmups = 1)
        @Warmup(iterations = 3)
        @OutputTimeUnit(TimeUnit.SECONDS)
        public void linkedHashMap() {
            for (int i = 0; i < count; i++) {
                linkedHashMap.put(i, String.valueOf(i));
            }
        }

    }

    @State(Scope.Thread)
    public static class UserGenerator {

        MockeyJockey mj = new MockeyJockey();

        Generator<Void, Integer> userIdGenerator = mj.integerSequences()
            .start(1)
            .diff(1);

        Set<String> ipAddresses = new HashSet<>();

        ParameterizedStringGenerator bitShiftGenerator = mj.parameterizedString("${0}.${1}.${2}.${3}")
            .param(mj.integers().min(1).max(254).transform(String.class, String::valueOf))
            .param(mj.integers().min(1).max(254).transform(String.class, String::valueOf))
            .param(mj.integers().min(1).max(254).transform(String.class, String::valueOf))
            .param(mj.integers().min(1).max(254).transform(String.class, String::valueOf));

        Generator<Void, String> ipAddressChooser = mj.custom(String.class, () -> {
            String result;

            do {
                result = bitShiftGenerator.get();
            } while (ipAddresses.contains(result));

            ipAddresses.add(result);
            return result;
        });

        ParameterizedStringGenerator emailGenerator = mj.parameterizedString("${0}@${1}.${2}")
            .param(new Supplier<String>() {
                @Override
                public String get() {
                    return "user_" + userIdGenerator.getLastGeneratedValue();
                }
            })
            .param(mj.randomSelection(String.class).withElements("gmail", "hotmail", "yandex"))
            .param(mj.randomSelection(String.class).withElements("com", "net", "org"));

        Generator<Void, Integer> creditCardBlockGenerator = mj.integers().min(1000).max(9999);

        Generator<Void, String> creditCardGenerator = mj.formattedString("%d-%d-%d-%d")
            .param(creditCardBlockGenerator)
            .param(creditCardBlockGenerator)
            .param(creditCardBlockGenerator)
            .param(creditCardBlockGenerator);

        Set<String> creditCards = new HashSet<>();

        Generator<Void, String> uniqueCreditCardGenerator = mj.custom(String.class, new Supplier<String>() {
            @Override
            public String get() {
                String result;

                do {
                    result = creditCardGenerator.get();
                } while (!creditCards.add(result));

                return result;
            }
        });

        ParameterizedStringGenerator mobileNumberGenerator = mj.parameterizedString("0${0}${1}")
            .param(mj.randomSelection(String.class).withElements("532", "533", "535", "536", "537", "538", "542", "543", "545", "551", "553", "555"))
            .param(mj.integers().min(1_000_000).max(9_999_999).transform(String.class, Object::toString));

        Map<String, Generator<Void, Platform>> platformGenerators = new HashMap<>();

        Generator<Void, String> randomPlatformChooser;

        CustomGenerator<Platform> randomPlatformGenerator = mj.custom(Platform.class, () -> {
            String randomPlatformName = randomPlatformChooser.get();
            return platformGenerators.get(randomPlatformName).get();
        });

        Generator<Void, User> userGenerator = mj.objectsFromReflection(User.class)
            .field("userId", userIdGenerator)
            .field("email", emailGenerator)
            .field("mobileNumber", mobileNumberGenerator)
            .field("name", mj.randomSelection(String.class).withElements(Constants.firstNames))
            .field("surname", mj.randomSelection(String.class).withElements(Constants.lastNames))
            .field("platform", randomPlatformGenerator)
            .field("creditCard", uniqueCreditCardGenerator);

        Generator<Void, Map<String, Object>> userMapGenerator = mj.maps()
            .field("userId", userIdGenerator)
            .field("email", emailGenerator)
            .field("mobileNumber", mobileNumberGenerator)
            .field("name", mj.randomSelection(String.class).withElements(Constants.firstNames))
            .field("surname", mj.randomSelection(String.class).withElements(Constants.lastNames))
            .field("platform", randomPlatformGenerator)
            .field("creditCard", uniqueCreditCardGenerator);

        Generator<Void, User> userConstructorGenerator = mj.objectsFromConstructor(User.class)
            .constructorParams(
                Arrays.asList(
                    userIdGenerator,
                    emailGenerator,
                    mobileNumberGenerator,
                    mj.randomSelection(String.class).withElements(Constants.firstNames),
                    mj.randomSelection(String.class).withElements(Constants.lastNames),
                    randomPlatformGenerator,
                    uniqueCreditCardGenerator
                )
            );

        @Setup(Level.Trial)
        public void setUp() {
            platformGenerators.put("web", mj.objectsFromReflection(Platform.class)
                .field("platform", mj.constant("web"))
                // .field("deviceId", mj.randomSelection(String.class).withElements(httpUserAgents))
                .field("deviceId", mj.custom(String.class, () -> "web-" + UUID.randomUUID().toString()))
                .field("ipAddress", ipAddressChooser));

            platformGenerators.put("android", mj.objectsFromReflection(Platform.class)
                .field("platform", mj.constant("android"))
                .field("deviceId", mj.custom(String.class, () -> "android-" + UUID.randomUUID().toString()))
                .field("ipAddress", ipAddressChooser));

            platformGenerators.put("ios", mj.objectsFromReflection(Platform.class)
                .field("platform", mj.constant("ios"))
                .field("deviceId", mj.custom(String.class, () -> "ios-" + UUID.randomUUID().toString()))
                .field("ipAddress", ipAddressChooser));

            randomPlatformChooser = mj.randomSelection(String.class).withElements(platformGenerators.keySet());
        }

        @Benchmark
        @Measurement(iterations = 5, time = 1)
        @Fork(value = 1, warmups = 1)
        @Warmup(iterations = 3)
        @OutputTimeUnit(TimeUnit.SECONDS)
        public User objectFromReflection() {
            return userGenerator.get();
        }

        @Benchmark
        @Measurement(iterations = 5, time = 1)
        @Fork(value = 1, warmups = 1)
        @Warmup(iterations = 3)
        @OutputTimeUnit(TimeUnit.SECONDS)
        public User constructor() {
            return userConstructorGenerator.get();
        }

        @Benchmark
        @Measurement(iterations = 5, time = 1)
        @Fork(value = 1, warmups = 1)
        @Warmup(iterations = 3)
        @OutputTimeUnit(TimeUnit.SECONDS)
        public Map<String, Object> map() {
            return userMapGenerator.get();
        }

    }

    @State(Scope.Thread)
    public static class RandomInt {

        MockeyJockey mj = new MockeyJockey();
        IntegerGenerator intGenerator = mj.integers();

        ObjectGenerator<Integer> rangerIntGenerator = random(range(0, Integer.MAX_VALUE));

        @Benchmark
        @Measurement(iterations = 5, time = 1)
        @Fork(value = 1, warmups = 1)
        @Warmup(iterations = 3)
        @OutputTimeUnit(TimeUnit.SECONDS)
        public int mockeyjockey() {
            return intGenerator.get();
        }

        @Benchmark
        @Measurement(iterations = 5, time = 1)
        @Fork(value = 1, warmups = 1)
        @Warmup(iterations = 3)
        @OutputTimeUnit(TimeUnit.SECONDS)
        public int ranger() {
            return rangerIntGenerator.next();
        }

    }

    @State(Scope.Thread)
    public static class RandomString {

        static final int strLen = 32;
        static final int listLen = 1_000_000;

        MockeyJockey mj = new MockeyJockey();
        StringGenerator stringGenerator = mj.strings().length(mj.constant(strLen));

        ObjectGenerator<String> rangerStringGenerator = randomContentString(constant(strLen));

        @Benchmark
        @Measurement(iterations = 5, time = 1)
        @Fork(value = 1, warmups = 1)
        @Warmup(iterations = 3)
        @OutputTimeUnit(TimeUnit.SECONDS)
        public String mockeyjockey() {
            return stringGenerator.get();
        }

        @Benchmark
        @Measurement(iterations = 5, time = 1)
        @Fork(value = 1, warmups = 1)
        @Warmup(iterations = 3)
        @OutputTimeUnit(TimeUnit.SECONDS)
        public String ranger() {
            return rangerStringGenerator.next();
        }

    }

    @State(Scope.Thread)
    public static class RandomStringList {

        static final int strLen = 32;
        static final int listLen = 1_000_000;

        MockeyJockey mj = new MockeyJockey();
        StringGenerator stringGenerator = mj.strings().length(mj.constant(strLen));

        ObjectGenerator<String> rangerStringGenerator = randomContentString(constant(strLen));

        @Benchmark
        @Measurement(iterations = 5, time = 1)
        @Fork(value = 1, warmups = 1)
        @Warmup(iterations = 3)
        @OutputTimeUnit(TimeUnit.SECONDS)
        public List<String> mockeyjockey() {
            return stringGenerator.list(mj.constant(listLen)).get();
        }

        @Benchmark
        @Measurement(iterations = 5, time = 1)
        @Fork(value = 1, warmups = 1)
        @Warmup(iterations = 3)
        @OutputTimeUnit(TimeUnit.SECONDS)
        public List<String> ranger() {
            List<String> strList = new ArrayList<>();
            for (int i = 0; i < listLen; i++) {
                strList.add(rangerStringGenerator.next());
            }
            return list(strList).next();
        }

    }

    @State(Scope.Thread)
    public static class StringInterpolation {

        MockeyJockey mj = new MockeyJockey();
        ParameterizedStringGenerator parameterizedStringGenerator = mj
            .parameterizedString("FIS_ACK_${0}")
            .param(mj.strings().length(mj.integers().min(42).max(62)));
        FormattedStringGenerator formattedStringGenerator = mj
            .formattedString("FIS_ACK_%s")
            .param(mj.strings().length(mj.integers().min(42).max(62)));
        ObjectGenerator<String> ranger = string("FIS_ACK_{}", randomContentString(random(range(42, 62))));

        @Benchmark
        @Measurement(iterations = 5, time = 1)
        @Fork(value = 1, warmups = 1)
        @Warmup(iterations = 3)
        @OutputTimeUnit(TimeUnit.SECONDS)
        public String parameterized() {
            return parameterizedStringGenerator.get();
        }

        @Benchmark
        @Measurement(iterations = 5, time = 1)
        @Fork(value = 1, warmups = 1)
        @Warmup(iterations = 3)
        @OutputTimeUnit(TimeUnit.SECONDS)
        public String formatted() {
            return formattedStringGenerator.get();
        }

        @Benchmark
        @Measurement(iterations = 5, time = 1)
        @Fork(value = 1, warmups = 1)
        @Warmup(iterations = 3)
        @OutputTimeUnit(TimeUnit.SECONDS)
        public String ranger() {
            return ranger.next();
        }

    }

    @State(Scope.Thread)
    public static class IpAddressGeneration {

        MockeyJockey mj = new MockeyJockey();

        private Generator<Void, String> sequentialGenerator = mj.ipAddressesSequential();
        private Generator<Void, String> random1KGenerator = mj.ipAddressesRandom().withBufferSize(1_000);
        private Generator<Void, String> random100KGenerator = mj.ipAddressesRandom().withBufferSize(100_000);

        @Benchmark
        @Measurement(iterations = 5, time = 1)
        @Fork(value = 1, warmups = 1)
        @Warmup(iterations = 3)
        @OutputTimeUnit(TimeUnit.SECONDS)
        public String sequential() {
            return sequentialGenerator.get();
        }

        @Benchmark
        @Measurement(iterations = 5, time = 1)
        @Fork(value = 1, warmups = 1)
        @Warmup(iterations = 3)
        @OutputTimeUnit(TimeUnit.SECONDS)
        public String random_1K() {
            return random1KGenerator.get();
        }

        @Benchmark
        @Measurement(iterations = 5, time = 1)
        @Fork(value = 1, warmups = 1)
        @Warmup(iterations = 3)
        @OutputTimeUnit(TimeUnit.SECONDS)
        public String random_100K() {
            return random100KGenerator.get();
        }

        @Benchmark
        @BenchmarkMode(Mode.AverageTime)
        @Measurement(iterations = 5, time = 1)
        @Fork(value = 1, warmups = 1)
        @Warmup(iterations = 3)
        @OutputTimeUnit(TimeUnit.SECONDS)
        public String sequential_1M() {
            String result = "";
            for (int i = 0; i < 1_000_000; i++) {
                result = sequentialGenerator.get();
            }
            return result;
        }

        @Benchmark
        @BenchmarkMode(Mode.AverageTime)
        @Measurement(iterations = 5, time = 1)
        @Fork(value = 1, warmups = 1)
        @Warmup(iterations = 3)
        @OutputTimeUnit(TimeUnit.SECONDS)
        public String random_1K_1M() {
            String result = "";
            for (int i = 0; i < 1_000_000; i++) {
                result = random1KGenerator.get();
            }
            return result;
        }

        @Benchmark
        @BenchmarkMode(Mode.AverageTime)
        @Measurement(iterations = 5, time = 1)
        @Fork(value = 1, warmups = 1)
        @Warmup(iterations = 3)
        @OutputTimeUnit(TimeUnit.SECONDS)
        public String random_100K_1M() {
            String result = "";
            for (int i = 0; i < 1_000_000; i++) {
                result = random100KGenerator.get();
            }
            return result;
        }

    }

}