package com.foodymon.businessapp.datastructure;

/**
 * Created by alexdai on 5/19/16.
 */
public class ServerException {

    private String errorCode;
    private String errorMessage;

    @Override
    public String toString() {
        return "Error Code: "+errorCode+", Message: " + errorMessage;
    }
}
