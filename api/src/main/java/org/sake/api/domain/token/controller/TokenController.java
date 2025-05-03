package org.sake.api.domain.token.controller;

import lombok.RequiredArgsConstructor;
import org.sake.api.common.api.Api;
import org.sake.api.common.error.TokenErrorCode;
import org.sake.api.common.exception.ApiException;
import org.sake.api.domain.token.business.TokenBusiness;
import org.sake.api.domain.token.controller.model.TokenResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class TokenController {

    private final TokenBusiness tokenBusiness;

    // access token 만료 시 클라이언트가 refresh token을 헤더에 넣어 서버에 요청하는 API
    @PostMapping("refresh")
    public Api<TokenResponse> refresh(
            @RequestHeader("X-Refresh-Token") String refreshToken
    ) {
        var response = tokenBusiness.rotateRefreshToken(refreshToken);
        return Api.OK(response);
    }

}
