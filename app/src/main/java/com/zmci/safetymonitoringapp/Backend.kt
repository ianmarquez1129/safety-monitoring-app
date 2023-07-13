package com.zmci.safetymonitoringapp

import android.content.Context
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.auth.AuthChannelEventName
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult
import com.amplifyframework.auth.options.AuthSignOutOptions
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.AmplifyConfiguration
import com.amplifyframework.core.InitializationStatus
import com.amplifyframework.hub.HubChannel
import com.amplifyframework.hub.HubEvent
import com.zmci.safetymonitoringapp.home.HomeFragment
import org.json.JSONArray

object Backend {

    private const val TAG = "Backend"

    fun initialize(applicationContext: Context) : Backend {
        try {
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            val config = AmplifyConfiguration.builder(applicationContext).devMenuEnabled(false).build()
            Amplify.configure(config, applicationContext)

            Log.i(TAG, "Initialized Amplify")
        } catch (e: AmplifyException) {
            Log.e(TAG, "Could not initialize Amplify", e)
        }

        Log.i(TAG, "registering hub event")

        // listen to auth event
        Amplify.Hub.subscribe(HubChannel.AUTH) { hubEvent: HubEvent<*> ->

            when (hubEvent.name) {
                InitializationStatus.SUCCEEDED.toString() -> {
                    Log.i(TAG, "Amplify successfully initialized")
                }
                InitializationStatus.FAILED.toString() -> {
                    Log.i(TAG, "Amplify initialization failed")
                }
                else -> {
                    when (AuthChannelEventName.valueOf(hubEvent.name)) {
                        AuthChannelEventName.SIGNED_IN -> {
                            updateUserData(true)
                            Log.i(TAG, "HUB : SIGNED_IN")
                        }
                        AuthChannelEventName.SIGNED_OUT -> {
                            updateUserData(false)
                            Log.i(TAG, "HUB : SIGNED_OUT")
                        }
                        else -> Log.i(TAG, """HUB EVENT:${hubEvent.name}""")
                    }
                }
            }
        }

        Log.i(TAG, "retrieving session status")

        // is user already authenticated (from a previous execution) ?
        Amplify.Auth.fetchAuthSession(
            { result ->
                Log.i(TAG, result.toString())
                val cognitoAuthSession = result as AWSCognitoAuthSession
                // update UI
                this.updateUserData(cognitoAuthSession.isSignedIn)
            },
            { error -> Log.i(TAG, error.toString()) }
        )
        return this

    }

    private fun updateUserData(withSignedInStatus : Boolean) {
        UserData.setSignedIn(withSignedInStatus)
    }

    fun signUp(username: String, password: String, email: String,) {
        val options = AuthSignUpOptions.builder()
            .userAttribute(AuthUserAttributeKey.email(), email)
            .build()
        Amplify.Auth.signUp(username, password, options,
            { Log.i("AuthQuickStart", "Sign up succeeded: $it")
            },
            { Log.e ("AuthQuickStart", "Sign up failed", it) }
        )
    }

    fun confirmSignUp(username: String, code: String) {
        Amplify.Auth.confirmSignUp(
            username, code,
            { result ->
                if (result.isSignUpComplete) {
                    Log.i("AuthQuickstart", "Confirm signUp succeeded")
                } else {
                    Log.i("AuthQuickstart","Confirm sign up not complete")
                }
            },
            { Log.e("AuthQuickstart", "Failed to confirm sign up", it) }
        )
    }

    fun signOut() {
        val options = AuthSignOutOptions.builder()
            .globalSignOut(true)
            .build()

        Amplify.Auth.signOut(options) { signOutResult ->
            when(signOutResult) {
                is AWSCognitoAuthSignOutResult.CompleteSignOut -> {
                    // handle successful sign out
                }
                is AWSCognitoAuthSignOutResult.PartialSignOut -> {
                    // handle partial sign out
                }
                is AWSCognitoAuthSignOutResult.FailedSignOut -> {
                    // handle failed sign out
                }
            }
        }
    }

    fun signIn(username : String, password : String) {
        Amplify.Auth.signIn(username, password,
            { result ->
                if (result.isSignedIn) {
                    Log.i("AuthQuickstart", "Sign in succeeded")
                } else {
                    Log.i("AuthQuickstart", "Sign in not complete")
                }
            },
            { Log.e("AuthQuickstart", "Failed to sign in", it) }
        )
    }

    fun fetchUserAttributes () {
        Amplify.Auth.fetchUserAttributes(
            { user ->
                val currentUserEmail = user[2].value.toString()
                UserData.setUserEmail(currentUserEmail)
            }, {
                Log.i("FetchUserAttribute", "Error fetch user attribute")
            }
        )
    }

    fun getLogs() {
        val request = RestOptions.builder()
            .addPath("/getLogs")
            .build()

        Amplify.API.get(request,
            { Log.i("MyAmplifyApp", "GET succeeded: ${it.data.asString()}")
                val data = JSONArray(it.data.asString())
                for (i in 0 until data.length()){
                    val item = data.getJSONObject(i)
                    val totalViolations = item.getString("total_violations")
                    Log.i("TotalDetections $i", totalViolations)
                    // for graph
                }
            },
            { Log.e("MyAmplifyApp", "GET failed.", it) }
        )
    }

    fun postRequest() {
        val options = RestOptions.builder()
            .addPath("/version")
            .addBody("{\"uuid\":\"ZMCI1\"}".encodeToByteArray())
            .build()

        Amplify.API.post(options,
            { Log.i("MyAmplifyApp", "POST succeeded: ${it.data.asString()}") },
            { Log.e("MyAmplifyApp", "POST failed", it) }
        )
    }

    fun getStatus(options: RestOptions) {
        Amplify.API.post(options,
            { Log.i("MyAmplifyApp", "POST succeeded: ${it.data.asString()}")
                val data = JSONArray(it.data.asString())
                for (i in 0 until data.length()) {
                    val item = data.getJSONObject(i)
                    val status = item.getString("status")
                    UserData.setDeviceStatus(status)
                }
            },
            { Log.e("MyAmplifyApp", "POST failed", it) }
        )
    }

}