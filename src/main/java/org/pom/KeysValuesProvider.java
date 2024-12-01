package org.pom;

import java.util.Collection;

public interface KeysValuesProvider<T> {
    Collection<T> values();
    Collection<T> keys();
}
