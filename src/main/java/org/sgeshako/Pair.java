package org.sgeshako;

import java.util.Objects;

public class Pair<T> {
    private final T first;
    private final T second;

    public Pair(T first, T second) {
        // Ensure consistent ordering (smallest first)
        if (first.hashCode() <= second.hashCode()) {
            this.first = first;
            this.second = second;
        } else {
            this.first = second;
            this.second = first;
        }
    }

    public T getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }

    public static <T> Pair<T> of(T first, T second) {
        return new Pair<>(first, second);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Pair<?> other)) return false;
        return Objects.equals(first, other.first) && Objects.equals(second, other.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}