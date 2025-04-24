package org.sake.api.domain.token.Ifs;

import org.sake.api.domain.token.entity.RefreshToken;
import org.sake.api.domain.token.model.TokenDto;

import java.util.Map;

public interface TokenHelperIfs {

    TokenDto issueAccessToken(Long userId);
    TokenDto issueRefreshToken(Long userId);

    Map<String, Object> validationTokenWithThrow(String token);

}
