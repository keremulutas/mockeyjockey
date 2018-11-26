## MockeyJockey project

MockeyJockey is a complex data generator. It is written to be able to generate data sets including cross-related objects
with a high performance and extensible API.

##### Initialization

Initializing MockeyJockey with a ThreadLocalRandom:

```
MockeyJockey.ThreadLocal u = new MockeyJockey.ThreadLocal();
```

##### Common Generator Methods

`.get()` will return a generated value.

`.withExplicitReset(boolean)` will help you control explicit reset feature. If explicit reset is set to `true`, then the
generator will return the last generated (generate one if not previously generated) value until an explicit call to
`.reset()` method.

`.accept(IT)` is used if the generator accepts input. Only used by ConstantGenerator.

`.getLastGeneratedValue()` will return the last generated value or `null`.

`.list(int)` and `.list(IntegerGenerator)` will return a ListGenerator which will generate a list including elements of
the calling generator's type.

`.transform(Class<T>, Function<OT, T>)` will return a new Generator, transforming the calling generator's output type.
For example, the following will generate String using an IntegerGenerator in 2 different ways:

```
Map<Integer, String> turkishNumbers = new HashMap<>();
turkishNumbers.put(0, "sıfır");
turkishNumbers.put(1, "bir");
turkishNumbers.put(2, "iki");
turkishNumbers.put(3, "üç");
turkishNumbers.put(4, "dört");
turkishNumbers.put(5, "beş");
turkishNumbers.put(6, "altı");
turkishNumbers.put(7, "yedi");
turkishNumbers.put(8, "sekiz");
turkishNumbers.put(9, "dokuz");

String str1 = mj.transform(
    String.class,
    mj.integers().min(0).max(10),
    (Function<Integer, String>) turkishNumbers::get
).get();

String str2 = mj.integers().min(0).max(10).transform(
    String.class,
    (Function<Integer, String>) turkishNumbers::get
).get();
```

#### Generator Types

##### Constants

```
ConstantGenerator<String> constantStrGenerator = mj.constant("Kerem Ulutaş");
```

##### Integers

The following generator will generate an integer in range `[0,15)`

```
IntegerGenerator randomIntGenerator1 = mj.integers().min(10).max(15);
```

Min and max bounds can also be other IntegerGenerator instances. For example, the following generator will generate an
integer in range `[random selection from [10, 15), 50)`

```
IntegerGenerator randomIntGenerator2 = mj.integers().min(randomIntGenerator1).max(50);
```

##### Longs

Usage is same with Integers. The following will generate a long between `[3L, 5L)`

```
LongGenerator longGenerator = mj.longs().min(3L).max(5L);
```

##### Doubles

Usage is same with Integers. DoubleGenerator also has a `.precision(int)` method, to set the precision. The following will
generate a double between `[3.00, 5.00)`

```
DoubleGenerator doubleGenerator = mj.doubles().min(3.0).max(5.0).precision(2);
```

##### Random Strings

By default strings are generated with random selections from the following subset:

`0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ`

There is also support for an extended subset, which includes characters:

``0123456789abcçdefgğhıijklmnoöpqrsştuüvwxyzABCÇDEFGĞHIİJKLMNOÖPQRSŞTUÜVWXYZ!"#$%&'()*+,-./:;<=>?@[\]^_`{|}``

Length can be a constant integer or an IntegerGenerator.

Here is a full example using extended subset:

```
StringGenerator randomLengthStringGenerator = mj.strings()
    .useExtendedSubset()
    .length(
        mj.integers().min(2).max(7)
    );
```

##### Parameterized Strings

Parameterized string generator generates strings by replacing placeholders with values from given StringGenerators.
Placeholders are in format `${0}, ${1}, ...` and replaced by insertion order. Here is a full example:

```
ParameterizedStringGenerator psg = mj.parameterizedString("${0}_Limit_${1}")
    .param(mj.strings().length(3))
    .param(mj.strings().length(mj.integers().min(3).max(7)));
```

A possible output of this generator might be `5sD_Limit_TY8x`.

##### Formatted Strings

Formatted strings follow [this](https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax) specification.
Like `.printf()`, arguments are placed in resulting string with insertion order. Here is a full example:

```
FormattedStringGenerator fsg = mj.formattedString("%s%04d")
    .param(mj.randomSelection(String.class).source(mj.constant(Arrays.asList("DG", "MJ"))))
    .param(mj.integers().min(0).max(1_000));
```

A possible output of this generator might be `DG0059`

##### Number Sequences

Numeric sequences can be generated like the following way:

```
NumericSequenceGenerator.Integers intSeqGenerator = mj.integerSequences()
    .start(1)
    .end(5)
    .diff(2);
```

This will generate numbers `1`, `3`, `5`, `1`, `3`, `...` in this order.

Generation will continue with the starting numeric value when `end` is reached.

If `.withStrictBoundaries(false)` is called, then generated sequence will be `1`, `3`, `5`, `2`, `4`, `1`, `3`, `...`

Generating double or long sequences are also possible.

##### Number Multiplexers

Number partitions is mostly used in periodic count calculations. For example, an integer list with 24 members might
represent hourly counts of transactions in a context. And an integer list with 7 members might represent daily counts of
the transactions.

Number partitioning is helpful in a way that applying hourly count ratios to other days' total counts.

For example, if hourly counts are all `1000` for a day, daily total would be `24 * 1000 = 24.000`
and if next day's daily total is `48.000`, then this next day's hourly counts will all be `2.000`

This generator takes numeric lists, and does partitioning in this manner.

A special corner case is occurred when sum of a given list is the first member in the next list. In this case, partition
generator will detect and skip this sum.

Full example for the scenario described above:

```
MultiplexerGenerator.Integers mg = mj.integersMultiplexer()
    .withSamples(Arrays.asList(1_000, 1_000, 1_000, 1_000, 1_000, 1_000, 1_000, 1_000, 1_000, 1_000, 1_000, 1_000, 1_000, 1_000, 1_000, 1_000, 1_000, 1_000, 1_000, 1_000, 1_000, 1_000, 1_000, 1_000))
    .withSamples(Arrays.asList(24_000, 48_000, 72_000))
```

This generator will generate `1.000` 24 times, `2.000` 24 times and `3.000` 24 times.

##### Numeric Partitions

Partitioning is finding list with `n` members that adds up to a given number `X`. In order to do that, MockeyJockey needs `X`,
part count `n`, and a maximum deviation value. This deviation value is the maximum difference between even parts and
generated numbers.

Full example:

```
PartitionGenerator.Integers intPartitionGenerator = u
    .integerPartitions(1_000, 3, 50);
```

A possible output of this generator might be `320, 344, 336, 308, 395, 297, 300, 361, 339` in order.

```
320 + 344 + 336 = 1.000
308 + 395 + 297 = 1.000
300 + 361 + 339 = 1.000
```

##### Sequential & Random Selection Generators

These generators will return either sequential or randomized selections from a given list of elements.
By default, these generators will retrieve source lists once and select from this list. If `.isCircular(true)` is called,
returned value will be removed from the source list and the source list will be retrieved again when the list's size is 0.

For input list:

```
List<String> hababamCharacters = Arrays.asList(
    "Kel Mahmut",
    "Badi Ekrem",
    "Külyutmaz",
    "Hüseyin Şevki Topuz",
    "İnek Şaban",
    "Güdük Necmi",
    "Damat Ferit",
    "Hafize Ana"
);
```

Sequential selection generator can be obtained this way:

```
SelectionGenerator.Sequential<String> sequentialSelector = mj.sequentialSelection(String.class)
    .source(mj.constant(hababamCharacters));
```

The output will be,

`Kel Mahmut`, `Badi Ekrem`, `Külyutmaz`, `Hüseyin Şevki Topuz`, `İnek Şaban`, `Güdük Necmi`, `Damat Ferit`, `Hafize Ana`,
`Kel Mahmut`, `Badi Ekrem`, `Külyutmaz`, `...` in order.

Randomized selection generator can be obtained this way:

```
SelectionGenerator.Randomized<String> randomizedSelector = mj.randomSelection(String.class)
    .source(mj.constant(hababamCharacters));
```

A possible output of this generator might be:

`Külyutmaz`, `Badi Ekrem`, `İnek Şaban`, `Badi Ekrem`, `Güdük Necmi`, `Hafize Ana`, `Badi Ekrem`, `Badi Ekrem`, `Külyutmaz`,
`Damat Ferit`, `Hüseyin Şevki Topuz`, `...`

##### Weighted Generator

Weighted generator can be used if percentage of elements in a set is known.

For example, given the `WeightedGenerator`:

```
WeightedGenerator<String> wg = mj.weighted(String.class)
    .add("bir", 2)
    .add("iki", 5)
    .add("üç", 3);
```

if we call `wg.get()` 10 times, we will get exactly 2 "bir", 5 "iki" and 3 "üç" values.

Weights can be given as generators, so possibilities are endless.

##### Transformer Generators

It is possible to transform the output of a generator to a completely different data type.

Example usage:

```
// String to Integer, strLen will be 20
int strLen = mj.constant("bosunasaymayirmiharf")
    .transform(Integer.class, (Function<String, Integer>) String::length)
    .get();
```

##### Custom Generators

Sometimes custom calculations might be needed when generating data. Custom data generators are mostly useful when generating
related fields in an object (for example a `Order.totalAmount` field which equals to the sum of `OrderItem.amount` fields
from a pre-generated `Order.items` field of type `List<OrderItem>`.

Self explanatory example usage:

```
IntegerGenerator randomIntGenerator = mj.integers().min(0).max(10_000);
String s = mj.custom(String.class, (Supplier<String>) () -> {
    String result;
    int value = randomIntGenerator.get();

    if(value < 100) result = "Lower than 100";
    else if(value < 1000) result = "Lower than 1000";
    else if(value > 5000) result = "Higher than 5000";
    else result = "Between 1000 and 5000";

    return result;
})
.get();
```

##### Object Generators

MockeyJockey is capable of generating objects by using reflection or constructor calls.
When generating object from constructor, order of items matter in `List<Generator<?, ?>` parameter, which
`.constructorParams()` take. Reflection generator does not have this limitation.

Here are the examples for both:

```
class OrderWith3Items {
    double totalAmount;
    double orderItem1Amount;
    double orderItem2Amount;
    double orderItem3Amount;

    public OrderWith3Items(double orderItem1Amount, double orderItem2Amount, double orderItem3Amount) {
        this.orderItem1Amount = orderItem1Amount;
        this.orderItem2Amount = orderItem2Amount;
        this.orderItem3Amount = orderItem3Amount;
        this.totalAmount = orderItem1Amount + orderItem2Amount + orderItem3Amount;
    }
}

DoubleGenerator doubleGenerator1 = mj.doubles().min(0.1).max(10.0).precision(2);
DoubleGenerator doubleGenerator2 = mj.doubles().min(5.0).max(15.0).precision(2);
DoubleGenerator doubleGenerator3 = mj.doubles().min(100.0).max(1000.0).precision(2);

ObjectGenerator.Reflection<OrderWith3Items> reflectionGenerator = mj.objectsFromReflection(OrderWith3Items.class)
    .field("orderItem1Amount", doubleGenerator1)
    .field("orderItem2Amount", doubleGenerator2)
    .field("orderItem3Amount", doubleGenerator3)
    .field("totalAmount", mj.custom(Double.class, (Supplier<Double>) () -> {
        double amount1 = doubleGenerator1.getLastGeneratedValue();
        double amount2 = doubleGenerator2.getLastGeneratedValue();
        double amount3 = doubleGenerator3.getLastGeneratedValue();
        return amount1 + amount2 + amount3;
    }));

ObjectGenerator.Constructor<OrderWith3Items> constructorGenerator = mj.objectsFromConstructor(OrderWith3Items.class)
    .constructorParams(
        Arrays.asList(
            doubleGenerator1,
            doubleGenerator2,
            doubleGenerator3,
            mj.custom(Double.class, (Supplier<Double>) () -> {
                double amount1 = doubleGenerator1.getLastGeneratedValue();
                double amount2 = doubleGenerator2.getLastGeneratedValue();
                double amount3 = doubleGenerator3.getLastGeneratedValue();
                return amount1 + amount2 + amount3;
            })
        )
    );
```

##### Map Generator

Generating maps are possible with MockeyJockey. This is extremely useful when dealing with data which will be represented in
JSON or when there is no Java class to represent the data with.

We can generate `OrderWith3Items` class as a map of type `Map<String, Object>`, like this:

```
MapGenerator mapGenerator = mj.maps()
    .field("orderItem1Amount", doubleGenerator1)
    .field("orderItem2Amount", doubleGenerator2)
    .field("orderItem3Amount", doubleGenerator3)
    .field("totalAmount", mj.custom(Double.class, (Supplier<Double>) () -> {
        double amount1 = doubleGenerator1.getLastGeneratedValue();
        double amount2 = doubleGenerator2.getLastGeneratedValue();
        double amount3 = doubleGenerator3.getLastGeneratedValue();
        return amount1 + amount2 + amount3;
    }));
```

You should consider down sides of representing an object as a `Map<String, Object>` (e.g. the need to cast
values to appropriate types)

### DONE

- Integer, Long, Double generator
- String, parameterized string & printf style formatted string generator
- List generator, generating list from an existing generator
- Object generator (Reflection)
- Object generator (Constructor)
- Weighted generator
- Custom generator
- Constant generator
- Integer, Long, Double partition generator
- Support for transforming a generator's output
- Integer, Long, Double multiplexer generator
- Reset mechanism
- Tag support for generators (.withTag("...")) - you can handle custom exceptions via MockeyJockeyException

### TODO

- UniqueGenerator (accepts a generator and internally keeps what has been generated before)
- WeightedGenerator addN and addEach methods
- property setters which can take constant values should have overrides (e.g. mj.strings().length method)
- Distribution (https://stackoverflow.com/questions/33730250/generating-probability-distributions-in-java)
- Probability
- Object generation (Factory Method)
- Related field generation
  (with context including partially generated object instance)
  (may not be implemented - reset mechanism with custom generator provides it smoothly)
- Convenience methods for resetting depending generators at once (for Object..Generator classes)
- WithFrequency overload to accept list of ratios for a given ChronoUnit in ZonedDateTimeGenerator

### Other Generators & Fakers

- https://github.com/tomacla/faker
- https://github.com/nomemory/mockneat/issues/17
- https://github.com/benas/random-beans/wiki/Key-APIs
- https://github.com/smartcat-labs/ranger

### Research

- https://stackoverflow.com/questions/19557829/faster-alternatives-to-javas-reflection
- https://stackoverflow.com/questions/22244402/how-can-i-improve-performance-of-field-set-perhap-using-methodhandles
- https://github.com/MukulShukla/xeger
- https://github.com/six2six/fixture-factory/blob/master/src/main/java/br/com/six2six/fixturefactory/function/impl/RegexFunction.java
- https://github.com/smartcat-labs/ranger/blob/dev/src/main/java/io/smartcat/ranger/distribution/UniformDistribution.java
- https://github.com/cbismuth/fast-random
- https://github.com/wmacevoy/kiss/blob/master/src/main/java/kiss/util/AESPRNG.java
- https://gist.github.com/Xyene/4637619
- http://www.baeldung.com/java-8-functional-interfaces
- https://github.com/sherxon/AlgoDS/tree/master/src/oi

- https://blog.dandyer.co.uk/2008/04/06/a-java-programmers-guide-to-random-numbers-part-2-not-just-coins-and-dice/
- http://commons.apache.org/proper/commons-rng/
- https://github.com/dwdyer/uncommons-maths (https://blog.dandyer.co.uk/2012/11/22/uncommons-maths-1-2-3/)
- https://cs.gmu.edu/~sean/research/ (Mersenne Twister)
- https://github.com/alexeyr/pcg-java
- http://dsiutils.di.unimi.it/

### Commands

For benchmarks:

```
./gradlew :benchmarks:run
```

To run samples:

```
./gradlew :samples:run
```