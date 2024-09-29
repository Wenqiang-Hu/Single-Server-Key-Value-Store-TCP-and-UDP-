

import java.util.HashMap;

/*
 * KeyValueStore uses Hashmap as the DS. It will be used for
 * bot TCP and UDP server to store the values.
 * 
 * Currently, there are three funcs: PUT, GET, DELETE for data manipulation.
 */
public class KeyValueStore {
    private HashMap<String, String> store;

    public KeyValueStore() {
        store = new HashMap<>();
    }

    public synchronized String put(String key, String value) {
        store.put(key, value);
        return "PUT: " + key + " = " + value;
    }

    public synchronized String get(String key) {
        if (store.containsKey(key)) {
            return "GET: " + key + " = " + store.get(key);
        } else {
            return "GET: " + key + " not found";
        }
    }

    public synchronized String delete(String key) {
        if (store.containsKey(key)) {
            store.remove(key);
            return "DELETE: " + key + " was removed";
        } else {
            return "DELETE: " + key + " not found";
        }
    }
}
