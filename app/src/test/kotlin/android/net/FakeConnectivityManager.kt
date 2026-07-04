package android.net

class FakeConnectivityManager : ConnectivityManager() {
    override fun isActiveNetworkMetered(): Boolean = false
}
