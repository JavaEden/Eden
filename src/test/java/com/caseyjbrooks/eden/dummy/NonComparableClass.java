package com.caseyjbrooks.eden.dummy;

public class NonComparableClass {
    public int value;

    public NonComparableClass(int value) { this.value = value; }

    public int compareTo(NonComparableClass rhs) {
        return this.value - rhs.value;
    }
}
