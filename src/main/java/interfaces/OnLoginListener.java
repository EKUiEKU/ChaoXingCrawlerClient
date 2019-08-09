package interfaces;

public interface OnLoginListener{
    void onLoginSuccess(Long uid);
    void onLoginFailure(String causeBy);
}