package com.example.firebaseauthdemoapp

import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel: ViewModel() {

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    //Check logged status, is user logged in?
    fun checkAuthStatus(){
        if (auth.currentUser==null){
            _authState.value = AuthState.Unauthenticated
        }else{
            _authState.value = AuthState.Authenticated
        }
    }

    //Email & password sign in
    //When user is logging in, it will loading first
    fun login(email : String,password : String){
        //Before calling the methode in the code block below
        if (email.isEmpty() || password.isEmpty()){
            _authState.value=  AuthState.Error ("Fields cannot be left blank")
            return //If empty then login won't be done
        }
        _authState.value = AuthState.Loading //Set it to loading before it can send the error msg
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{task ->
                if (task.isSuccessful){ //if logging in is successful
                    _authState.value = AuthState.Authenticated
                }else { //error msg
                _authState.value=  AuthState.Error (task.exception?.message?:"An unexpected error occured")

                }
            }

    }

}


sealed class AuthState{
    object Authenticated : AuthState() //To get to know which state we are in
    object Unauthenticated : AuthState ()
    object Loading : AuthState()
    data class Error(val message: String): AuthState()

}