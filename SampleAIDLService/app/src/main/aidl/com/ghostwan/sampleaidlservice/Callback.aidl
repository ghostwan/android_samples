// Result.aidl
package com.ghostwan.sampleaidlservice;

// Declare any non-default types here with import statements

interface Callback {
     void onSuccess(String response);
     void onError(int errorCode, String errorMessage);
}
