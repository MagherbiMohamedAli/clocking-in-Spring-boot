package tn.biramgroup.pointage.springSecurityJwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tn.biramgroup.pointage.model.User;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil {
    private static final long EXPIRE_DURATION = 24 * 60 * 60 * 1000 * 7; // 1 week
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Value("${app.jwt.secret}")
    private String SECRET_KEY;

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(String.format("%s,%s", user.getUserId(), user.getEmail()))
                .claim("roles", user.getRoles())
                .setIssuer("BFL")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }
    public List<String> getRoles(String token) {
        Claims claims = parseClaims(token);
        return claims.get("roles", List.class);
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            System.out.println("JWT expired, "+ ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.out.println("error, "+ ex.getMessage());
        } catch (MalformedJwtException ex) {
            System.out.println("JWT invalid, "+ ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            System.out.println("JWT is not supported, "+ ex.getMessage());
        } catch (SignatureException ex) {
            System.out.println("Signature validation failed, "+ ex.getMessage());

        }

        return false;
    }


    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
