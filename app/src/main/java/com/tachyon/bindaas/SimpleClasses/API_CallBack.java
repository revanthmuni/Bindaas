package com.tachyon.bindaas.SimpleClasses;

import java.util.ArrayList;


public interface API_CallBack {

    void ArrayData(ArrayList arrayList);

    void OnSuccess(String responce);

    void OnFail(String responce);


}
