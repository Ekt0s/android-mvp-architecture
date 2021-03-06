/*
 * Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://mindorks.com/license/apache-v2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.mindorks.framework.mvp.ui.login;

import com.mindorks.framework.mvp.R;
import com.mindorks.framework.mvp.data.DataManager;
import com.mindorks.framework.mvp.data.network.model.LoginRequest;
import com.mindorks.framework.mvp.data.network.model.LoginResponse;
import com.mindorks.framework.mvp.utils.rx.TestSchedulerProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by amitshekhar on 02/02/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {

    @Mock
    LoginMvpView mMockLoginMvpView;
    @Mock
    DataManager mMockDataManager;

    private LoginPresenter<LoginMvpView> mLoginPresenter;
    private TestScheduler mTestScheduler;

    @BeforeClass
    public static void onlyOnce() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(mTestScheduler);
        mLoginPresenter = new LoginPresenter<>(
                mMockDataManager,
                testSchedulerProvider,
                compositeDisposable);

        mLoginPresenter = spy(mLoginPresenter);
        mLoginPresenter.onAttach(mMockLoginMvpView);
    }

    @Test
    public void testServerLoginSuccess() {

        String email = "dummy@gmail.com";
        String password = "password";

        LoginResponse loginResponse = new LoginResponse();

        doReturn(Observable.just(loginResponse))
                .when(mMockDataManager)
                .doServerLoginApiCall(new LoginRequest
                        .ServerLoginRequest(email, password));

        mLoginPresenter.onServerLoginClick(email, password);

        mTestScheduler.triggerActions();

        verify(mMockLoginMvpView).showLoading();
        verify(mMockLoginMvpView).hideLoading();
        verify(mMockLoginMvpView).openMainActivity();
    }

    @Test
    public void testServerLoginErrorMessageWithEmptyPassword() throws Exception {

        String email = "dummy@gmail.com";
        String password = "";

        mLoginPresenter.onServerLoginClick(email, password);

        mTestScheduler.triggerActions();

        verify(mMockLoginMvpView).onError(R.string.empty_password);
    }

    @Test
    public void testServerLoginErrorMessageWithNullPassword() throws Exception {

        String email = "dummy@gmail.com";
        String password = null;

        mLoginPresenter.onServerLoginClick(email, password);

        mTestScheduler.triggerActions();

        verify(mMockLoginMvpView).onError(R.string.empty_password);
    }

    @Test
    public void testServerLoginErrorMessageWithNullEmail() throws Exception {

        String email = null;
        String password = "";

        mLoginPresenter.onServerLoginClick(email, password);

        mTestScheduler.triggerActions();

        verify(mMockLoginMvpView).onError(R.string.empty_email);
    }

    @Test
    public void testServerLoginErrorMessageWithEmptyEmail() throws Exception {

        String email = "";
        String password = "";

        mLoginPresenter.onServerLoginClick(email, password);

        mTestScheduler.triggerActions();

        verify(mMockLoginMvpView).onError(R.string.empty_email);
    }

    @Test
    public void testServerLoginErrorMessageWithInvalidEmail() throws Exception {

        String email = "dummy@gmail.com";
        String password = "password";

        when(mLoginPresenter.isEmailValid(anyString())).thenReturn(false);

        mLoginPresenter.onServerLoginClick(email, password);

        mTestScheduler.triggerActions();

        verify(mMockLoginMvpView).onError(R.string.invalid_email);
    }

    @Test
    public void testGoogleLoginSucceed() throws Exception {
        final String googleUserID = "test1";
        final String idToken = "test1";

        LoginResponse loginResponse = new LoginResponse();

        // When
        doReturn(Observable.just(loginResponse))
                .when(mMockDataManager)
                .doGoogleLoginApiCall(new LoginRequest.
                        GoogleLoginRequest(googleUserID, idToken));

        mLoginPresenter.onGoogleLoginClick();

        mTestScheduler.triggerActions();

        verify(mMockLoginMvpView).showLoading();
        verify(mMockLoginMvpView).hideLoading();
        verify(mMockLoginMvpView).openMainActivity();
    }

    @After
    public void tearDown() throws Exception {
        mLoginPresenter.onDetach();
    }

}