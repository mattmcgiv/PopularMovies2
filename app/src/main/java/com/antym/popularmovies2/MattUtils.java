package com.antym.popularmovies2;

/**
 * Created by matthewmcgivney on 11/21/15.
 */
public class MattUtils {
    public static void ifNullThrowException(Object o) {
        if (o == null) {
            new Exception("Object is null").printStackTrace();
        }
    }
}
