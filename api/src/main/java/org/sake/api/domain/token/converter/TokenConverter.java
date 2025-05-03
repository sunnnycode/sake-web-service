package org.sake.api.domain.token.converter;

import lombok.RequiredArgsConstructor;
import org.sake.api.common.annotation.Converter;
import org.sake.api.common.error.ErrorCode;
import org.sake.api.common.exception.ApiException;
import org.sake.api.domain.token.controller.model.TokenResponse;
import org.sake.api.domain.token.model.TokenDto;

import java.util.Objects;

@RequiredArgsConstructor
@Converter
public class TokenConverter {

    public TokenResponse toResponse(
            TokenDto accessToken,
            TokenDto refreshToken
    ){
        Objects.requireNonNull(accessToken, ()->{throw new ApiException(ErrorCode.NULL_POINT);});
        Objects.requireNonNull(refreshToken, ()->{throw new ApiException(ErrorCode.NULL_POINT);});

        return TokenResponse.builder()
                .accessToken(accessToken.getAccessToken())
                .accessTokenExpiredAt(accessToken.getAccessTokenExpiredAt())
                .refreshToken(refreshToken.getRefreshToken())
                .refreshTokenExpiredAt(refreshToken.getRefreshTokenExpiredAt())
                .build();
    }
}
