package edu.aku.hassannaqvi.naunehal_remidline.base.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.aku.hassannaqvi.naunehal_remidline.base.repository.ResponseStatusCallbacks
import edu.aku.hassannaqvi.naunehal_remidline.base.usecase.LoginUsecase
import edu.aku.hassannaqvi.naunehal_remidline.models.Users
import kotlinx.coroutines.launch

class LoginViewModel(private val loginUsecase: LoginUsecase) : ViewModel() {

    private val _loginResponse: MutableLiveData<ResponseStatusCallbacks<Users>> = MutableLiveData()

    val loginResponse: MutableLiveData<ResponseStatusCallbacks<Users>>
        get() = _loginResponse

    fun getLoginInfoFromDB(username: String, password: String) {
        _loginResponse.value = ResponseStatusCallbacks.loading(null)
        viewModelScope.launch {
            try {
                val loginData = loginUsecase(username, password)
                _loginResponse.value =
                    ResponseStatusCallbacks.success(data = loginData, "User exist")
            } catch (e: Exception) {
                _loginResponse.value = ResponseStatusCallbacks.error(null, e.message.toString())
            }
        }
    }

}