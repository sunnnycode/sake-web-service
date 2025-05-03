package org.sake.api.domain.token.business;

import lombok.RequiredArgsConstructor;
import org.sake.api.common.annotation.Business;
import org.sake.api.common.error.ErrorCode;
import org.sake.api.common.exception.ApiException;
import org.sake.api.domain.token.controller.model.TokenResponse;
import org.sake.api.domain.token.converter.TokenConverter;
import org.sake.api.domain.token.model.TokenDto;
import org.sake.api.domain.token.service.TokenService;
import org.sake.db.user.UserEntity;

import java.util.Optional;

@RequiredArgsConstructor
@Business
public class TokenBusiness {

    private final TokenService tokenService;

    private final TokenConverter tokenConverter;

    // userEntity에서 userId 추출 후, access token, refresh token 발행
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

    // token 검증
    public Long validationAccessToken(String accessToken){
        var userId = tokenService.validationToken(accessToken);
        return userId;
    }

    // Refresh Token Rotation 기법
    public TokenResponse rotateRefreshToken(String refreshToken){

        Long userId = tokenService.rotateRefreshToken(refreshToken);

        // 새 token 발급
        TokenDto accessTokenDto = tokenService.issueAccessToken(userId);
        TokenDto refreshTokenDto = tokenService.issueRefreshToken(userId);

        return tokenConverter.toResponse(accessTokenDto, refreshTokenDto);
    }

}
