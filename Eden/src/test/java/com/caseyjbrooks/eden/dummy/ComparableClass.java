package com.caseyjbrooks.eden.dummy;

public class ComparableClass implements Comparable<ComparableClass> {
    public int value;

    public ComparableClass(int value) { this.value = value; }

    @Override
    public int compareTo(ComparableClass rhs) {
        return this.value - rhs.value;
    }
}
