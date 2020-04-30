// ITestService.aidl
package com.ghostwan.sampleaidlservice;
import com.ghostwan.sampleaidlservice.Callback;

// Declare any non-default types here with import statements
interface ITestService {
    void getInfo(in Callback callback);
}
