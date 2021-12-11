package edu.aku.hassannaqvi.naunehal_remidline.base.usecase

import edu.aku.hassannaqvi.naunehal_remidline.base.repository.GeneralRepository

class LoginUsecase(val repository: GeneralRepository) {
    suspend operator fun invoke(
        username: String, password: String
    ) = repository.getLoginInformation(
        username, password
    )
}