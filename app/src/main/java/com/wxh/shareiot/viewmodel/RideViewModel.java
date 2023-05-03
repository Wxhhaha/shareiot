package com.wxh.shareiot.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.wxh.basiclib.http.HttpManager;
import com.wxh.basiclib.http.IHttpResult;
import com.wxh.basiclib.lifecycle.BaseViewModel;
import com.wxh.shareiot.api.ApiHolder;
import com.wxh.shareiot.api.CycleListResponse;
import com.wxh.shareiot.api.CycleTraceResponse;
import com.wxh.shareiot.api.Error;
import com.wxh.shareiot.utils.UserUtil;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RideViewModel extends BaseViewModel {
    private MutableLiveData<CycleListResponse> cycleListResult = new MutableLiveData<>();
    private MutableLiveData<CycleTraceResponse> traceListResult = new MutableLiveData<>();

    public MutableLiveData<CycleListResponse> getCycleListResult() {
        return cycleListResult;
    }

    public MutableLiveData<CycleTraceResponse> getTraceListResult() {
        return traceListResult;
    }


    /**
     * 查询骑行列表
     */
    public void findCycleList(int pageNum) {
        showDialog.setValue(true);
        JSONObject object = new JSONObject();
        try {
            object.put("pageNumber", pageNum);
            object.put("cycleStatus", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
        Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().findCycleList(UserUtil.token, body),
                new IHttpResult<CycleListResponse>() {
                    @Override
                    public void success(CycleListResponse myResult) {
                        showDialog.setValue(false);
                        cycleListResult.setValue(myResult);

                    }

                    @Override
                    public void error(int i, String s) {
                        showDialog.setValue(false);
                        error.setValue(new Error(i, s));
                    }
                });
        addDisposable(disposable);
    }

    /**
     * 查询骑行轨迹列表
     */
    public void findCycleTrace(String cycleId) {
        showDialog.setValue(true);
        JSONObject object = new JSONObject();
        try {
            object.put("cycleId", cycleId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
        Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().findTrace(UserUtil.token, body),
                new IHttpResult<CycleTraceResponse>() {
                    @Override
                    public void success(CycleTraceResponse myResult) {
                        showDialog.setValue(false);
                        traceListResult.setValue(myResult);
                    }

                    @Override
                    public void error(int i, String s) {
                        showDialog.setValue(false);
                        error.setValue(new Error(i, s));
                    }
                });
        addDisposable(disposable);
    }
}
