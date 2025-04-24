package org.sake.api.domain.token.business;

import lombok.RequiredArgsConstructor;
import org.sake.api.common.annotation.Business;
import org.sake.api.common.error.ErrorCode;
import org.sake.api.common.exception.ApiException;
import org.sake.api.domain.token.controller.model.TokenResponse;
import org.sake.api.domain.token.converter.TokenConverter;
import org.sake.api.domain.token.service.TokenService;
import org.sake.db.user.UserEntity;

import java.util.Optional;

@RequiredArgsConstructor
@Business
public class TokenBusiness {

    private final TokenService tokenService;

    private final TokenConverter tokenConverter;

    /**
     * 1. user entity user Id 추출
     * 2. access, refresh token 발행
     * 3. converter -> token response로 변경
     */

    public TokenResponse issueToken(UserEntity userEntity){

        return Optional.ofNullable(userEntity)
                .map(user -> {
                    return user.getId();
                })
                .map(userId -> {
                    var accessToken = tokenService.issueAccessToken(userId);
                    var refreshToken = tokenService.issueRefreshToken(userId);
                    return tokenConverter.toResponse(accessToken, refreshToken);
                })
                .orElseThrow(
                        ()-> new ApiException(ErrorCode.NULL_POINT)
                );


    }

    public Long validationAccessToken(String accessToken){
        var userId = tokenService.validationToken(accessToken);
        return userId;
    }
}
