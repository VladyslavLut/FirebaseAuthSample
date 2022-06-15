package ua.vladyslavlut.firebaseauthsample.error

class FirebaseAuthNotInitialized : RuntimeException("FirebaseAuthNotInitialized")
class UserIsNullError : RuntimeException("UserIsNullError")
class TokenIdIsNullError : RuntimeException("TokenIdIsNullError")
class InvalidVerificationCodeError : RuntimeException("InvalidVerificationCodeError")
class UnexpectedError : RuntimeException("UnexpectedError")