package org.sake.api.domain.token.service;

import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.sake.api.common.error.ErrorCode;
import org.sake.api.common.error.TokenErrorCode;
import org.sake.api.common.exception.ApiException;
import org.sake.api.domain.token.Ifs.TokenHelperIfs;
import org.sake.api.domain.token.controller.model.TokenResponse;
import org.sake.api.domain.token.entity.RefreshToken;
import org.sake.api.domain.token.entity.RefreshTokenRepository;
import org.sake.api.domain.token.model.TokenDto;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;


@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenHelperIfs tokenHelperIfs;
    private final RefreshTokenRepository refreshTokenRepository;

    // access token 생성
    public TokenDto issueAccessToken(Long userId) {
        return tokenHelperIfs.issueAccessToken(userId);
    }

    // refresh token 생성
    public TokenDto issueRefreshToken(Long userId) {
        return tokenHelperIfs.issueRefreshToken(userId);
    }

    // token 검증
    public Long validationToken(String token) {
        // subject == userId
        String subject = tokenHelperIfs.validationTokenWithThrow(token);
        Objects.requireNonNull(subject, () -> {
            throw new ApiException(ErrorCode.NULL_POINT);
        });

        return Long.parseLong(subject);
    }

    // Refresh Token Rotation
    public Long rotateRefreshToken(String token) {

        // 토큰 유효성 검증(서명, 만료)
        try {
            tokenHelperIfs.validationTokenWithThrow(token);

        } catch (Exception e) {
            if (e instanceof SignatureException) {
                throw new ApiException(TokenErrorCode.INVALID_TOKEN);
            }
        }

        // Redis에 저장된 refresh token
        RefreshToken oldRt = refreshTokenRepository.findById(token)
                .orElseThrow(() -> new ApiException(TokenErrorCode.TOKEN_MISMATCH));

        // Redis에 저장된 토큰과 일치하지 않는 경우 제외
        if(!oldRt.getRefreshToken().equals(token)) {
            throw new ApiException(TokenErrorCode.TOKEN_MISMATCH);
        }

        // userId 추출
        Long userId = oldRt.getUserId();

        // 기존 refresh token은 Redis에서 삭제
        refreshTokenRepository.deleteById(token);

        return userId;


    }
}
