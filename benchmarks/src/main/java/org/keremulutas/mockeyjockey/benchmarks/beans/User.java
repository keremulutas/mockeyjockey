package org.keremulutas.mockeyjockey.benchmarks.beans;

import java.util.Objects;

public class User {

    public int userId;
    public String email;
    public String mobileNumber;
    public String name;
    public String surname;
    public Platform platform;
    public String creditCard;

    public User() {

    }

    public User(int userId, String email, String mobileNumber, String name, String surname, Platform platform, String creditCard) {
        this.userId = userId;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.name = name;
        this.surname = surname;
        this.platform = platform;
        this.creditCard = creditCard;
    }

    public User(User source) {
        this.userId = source.userId;
        this.email = source.email;
        this.mobileNumber = source.mobileNumber;
        this.name = source.name;
        this.surname = source.surname;
        this.platform = source.platform.clone();
        this.creditCard = source.creditCard;
    }

    public User clone() {
        return new User(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!( o instanceof User )) {
            return false;
        }
        User user = (User) o;
        return userId == user.userId &&
            Objects.equals(email, user.email) &&
            Objects.equals(mobileNumber, user.mobileNumber) &&
            Objects.equals(name, user.name) &&
            Objects.equals(surname, user.surname) &&
            Objects.equals(platform, user.platform) &&
            Objects.equals(creditCard, user.creditCard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, email, mobileNumber, name, surname, platform, creditCard);
    }

    @Override
    public String toString() {
        return "User{" +
            "userId=" + userId +
            ", email='" + email + '\'' +
            ", mobileNumber='" + mobileNumber + '\'' +
            ", name='" + name + '\'' +
            ", surname='" + surname + '\'' +
            ", platform=" + platform + '\'' +
            ", creditCard=" + creditCard +
            '}';
    }

}
