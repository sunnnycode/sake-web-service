package org.sake.api.domain.token.helper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.sake.api.common.error.TokenErrorCode;
import org.sake.api.common.exception.ApiException;
import org.sake.api.domain.token.Ifs.TokenHelperIfs;
import org.sake.api.domain.token.entity.RefreshToken;
import org.sake.api.domain.token.entity.RefreshTokenRepository;
import org.sake.api.domain.token.model.TokenDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenHelper implements TokenHelperIfs {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${TOKEN_SECRET_KEY}")
    private String secretKey;

    @Value("${TOKEN_ACCESS_PLUS_HOUR}")
    private Long accessTokenPlusHour;

    @Value("${TOKEN_REFRESH_PLUS_HOUR}")
    private Long refreshTokenPlusHour;

    // access token 생성
    @Override
    public TokenDto issueAccessToken(Long userId) {
        var expiredLocaldateTime = LocalDateTime.now().plusHours(accessTokenPlusHour);

        var expiredAt = Date.from(
                expiredLocaldateTime.atZone(
                        ZoneId.systemDefault()
                ).toInstant()
        );

        var key = Keys.hmacShaKeyFor(secretKey.getBytes());

        var jwtToken = Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS256)
                .setSubject(String.valueOf(userId))
                .setExpiration(expiredAt)
                .compact();

        return TokenDto.builder()
                .accessToken(jwtToken)
                .accessTokenExpiredAt(expiredLocaldateTime)
                .build();

    }

    // refresh token 생성
    @Override
    public TokenDto issueRefreshToken(Long userId) {
        var expiredLocaldateTime = LocalDateTime.now().plusHours(refreshTokenPlusHour);

        var expiredAt = Date.from(
                expiredLocaldateTime.atZone(
                        ZoneId.systemDefault()
                ).toInstant()
        );

        var key = Keys.hmacShaKeyFor(secretKey.getBytes());

        // refreshToken 생성
        String jwtToken = Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS256)
                .setSubject(String.valueOf(userId))
                .setExpiration(expiredAt)
                .compact();

        // Redis에 저장
        RefreshToken token = RefreshToken.builder()
                .refreshToken(jwtToken)
                .userId(userId)
                .build();

        refreshTokenRepository.save(token);

        // 클라이언트에게 반환 할 JWT
        return TokenDto.builder()
                .refreshToken(jwtToken)
                .refreshTokenExpiredAt(LocalDateTime.now().plusHours(refreshTokenPlusHour)) // 토큰 만료 시간
                .build();

    }

    // token 검증
    @Override
    public Map<String, Object> validationTokenWithThrow(String token) {
        var key = Keys.hmacShaKeyFor(secretKey.getBytes());

        var parser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();

        try{
            var result = parser.parseClaimsJws(token);
            return new HashMap<String, Object>(result.getBody());

        }catch (Exception e){
            if(e instanceof SignatureException){
               // 토큰이 유효하지 않을 때
                throw new ApiException(TokenErrorCode.INVALID_TOKEN);
            }
            else if(e instanceof ExpiredJwtException){
                // 만료된 토큰
                throw new ApiException(TokenErrorCode.EXPIRED_TOKEN);
            }
            else{
                // 그 외 에러
                throw new ApiException(TokenErrorCode.TOKEN_EXCEPTION);
            }
        }
    }
}
