package org.sake.api.domain.token.service;

import lombok.RequiredArgsConstructor;
import org.sake.api.common.error.ErrorCode;
import org.sake.api.common.exception.ApiException;
import org.sake.api.domain.token.Ifs.TokenHelperIfs;
import org.sake.api.domain.token.model.TokenDto;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;

/**
 * token 에 대한 도메인로직
 */
@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenHelperIfs tokenHelperIfs;

    public TokenDto issueAccessToken(Long userId){


        return tokenHelperIfs.issueAccessToken(userId);

    }

    public TokenDto issueRefreshToken(Long userId){

        return tokenHelperIfs.issueRefreshToken(userId);

    }

    public Long validationToken(String token){
        var map = tokenHelperIfs.validationTokenWithThrow(token);
        var userId = map.get("userId");
        Objects.requireNonNull(userId, () ->{throw new ApiException(ErrorCode.NULL_POINT);});
        return Long.parseLong(userId.toString());


    }
}
