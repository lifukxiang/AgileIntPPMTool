package io.intelligence.ppmtool.security;

public class SecurityConstants {

    public final static String SIGN_UP_URLS = "/api/users/**";
    public final static String H2_URL = "/h2-console/**";
    public final static String SECRET = "SecretKeyToGenJWTs";
    public final static String TOKEN_PREFIX = "Bearer ";
    public final static String HEADER_STRING = "Authorization";
    public final static long EXPIRATION_TIME = 3600_000; //3600 seconds

}
