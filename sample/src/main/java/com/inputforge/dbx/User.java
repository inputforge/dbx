package com.inputforge.dbx;

import java.time.Instant;
import java.util.Objects;

public final class User {
    private final long id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
    private final boolean isActive;
    private final boolean isDeleted;
    private final Instant createdAt;
    private final Instant updatedAt;

    public User(long id,
                String firstName,
                String lastName,
                String email,
                String password,
                boolean isActive,
                boolean isDeleted,
                Instant createdAt,
                Instant updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.isActive = isActive;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long id() {
        return id;
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public String email() {
        return email;
    }

    public String password() {
        return password;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (User) obj;
        return this.id == that.id &&
                Objects.equals(this.firstName, that.firstName) &&
                Objects.equals(this.lastName, that.lastName) &&
                Objects.equals(this.email, that.email) &&
                Objects.equals(this.password, that.password) &&
                this.isActive == that.isActive &&
                this.isDeleted == that.isDeleted &&
                Objects.equals(this.createdAt, that.createdAt) &&
                Objects.equals(this.updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, password, isActive, isDeleted,
                createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "User[" +
                "id=" + id + ", " +
                "firstName=" + firstName + ", " +
                "lastName=" + lastName + ", " +
                "email=" + email + ", " +
                "password=" + password + ", " +
                "isActive=" + isActive + ", " +
                "isDeleted=" + isDeleted + ", " +
                "createdAt=" + createdAt + ", " +
                "updatedAt=" + updatedAt + ']';
    }

}
