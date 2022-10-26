package com.proxym.poleactualities.Security.Services;

import com.proxym.poleactualities.Models.PasswordResetToken;
import com.proxym.poleactualities.Models.User;


import java.util.List;

public interface passwordResetTokenService {
    public PasswordResetToken getToken(String Token);
    public List<PasswordResetToken> getAllTokenByUser(User user);
    public void saveToken(PasswordResetToken token);
}
