package Entities;

import java.io.Serializable;

/**
 * Created by Johan on 01/06/15.
 */
public class Pair<K, V> implements Serializable {

    K key;
    V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public V getValue() {
        return (V) value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public K getKey() {
        return (K) key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Pair) {
            Pair pair = (Pair) o;
            if (key != null ? !key.equals(pair.key) : pair.key != null) return false;
            if (value != null ? !value.equals(pair.value) : pair.value != null) return false;
            return true;
        }
        return false;
    }


    @Override
    public int hashCode() {
        // name's hashCode is multiplied by an arbitrary prime number (13)
        // in order to make sure there is a difference in the hashCode between
        // these two parameters:
        //  name: a  value: aa
        //  name: aa value: a
        return key.hashCode() * 13 + (value == null ? 0 : value.hashCode());
    }

}
