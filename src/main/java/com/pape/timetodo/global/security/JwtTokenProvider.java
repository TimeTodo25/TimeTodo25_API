package com.pape.timetodo.global.security;

import com.pape.timetodo.global.exception.AppException;
import com.pape.timetodo.global.exception.ExceptionCode;
import com.pape.timetodo.global.jpa.repository.UsersRepository;
import com.pape.timetodo.global.security.model.TokenModel;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@PropertySource("classpath:jwt.yml")
@Service
@Slf4j
public class JwtTokenProvider implements InitializingBean {

    @Autowired
    private UsersRepository usersRepository;

    @Value("${secret-key}")
    private String secret;

    private static final String AUTHORITIES_KEY = "auth";
    
    private Key key;
    
    private final long tokenValidityInMilliseconds;

    private final long tokenValidityInDay;

    @Autowired
    private HttpServletRequest request;
    

    public JwtTokenProvider(
        @Value("${expiration-minute}") long tokenValidityInMinute,
        @Value("${expriation-day}") long tokenValidityInDay
    ) {        
        this.tokenValidityInMilliseconds = tokenValidityInMinute * 60 * 1000;
        this.tokenValidityInDay = tokenValidityInDay * 1000 * 60 * 60 * 24;
    }


    
    // 빈이 생성되고 주입을 받은 후에 secret값을 Base64 Decode해서 key 변수에 할당하기 위해
    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenModel createToken(Authentication authentication) {

        String username = authentication.getName();

        if(!usersRepository.findById(username).isPresent()) throw new AppException(ExceptionCode.DATA_NOT_FIND);

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 토큰의 expire 시간을 설정
        long now = (new Date()).getTime();
        Date accessValidity = new Date(now + this.tokenValidityInMilliseconds);
        Date refreshValidity = new Date(now + this.tokenValidityInDay);

        String accessToken = Jwts.builder()
                .setSubject(username)
                .claim(AUTHORITIES_KEY, authorities) // 정보 저장
                .signWith(key, SignatureAlgorithm.HS512) // 사용할 암호화 알고리즘과 , signature 에 들어갈 secret값 세팅
                .setExpiration(accessValidity) // set Expire Time 해당 옵션 안넣으면 expire안함
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .setExpiration(refreshValidity)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return TokenModel.builder()
                .grantType(AuthConstants.TOKEN_TYPE.getValue())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 토큰값으로 Username 가져옴
     * @param token
     * @return
     */
    public String getSubject(String token){

        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }    

    // 토큰으로 클레임을 만들고 이를 이용해 유저 객체를 만들어서 최종적으로 authentication 객체를 리턴
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰의 유효성 검증을 수행
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            
            log.info("잘못된 JWT 서명입니다.");
            throw new AppException(ExceptionCode.INVALID_JWT_SIGN);
        } catch (ExpiredJwtException e) {
            
            log.info("만료된 JWT 토큰입니다.");
            throw new AppException(ExceptionCode.EXPRIED_JWT);
        } catch (UnsupportedJwtException e) {
            
            log.info("지원되지 않는 JWT 토큰입니다.");
            throw new AppException(ExceptionCode.UNSUPPORTED_JWT);
        } catch (IllegalArgumentException e) {
            
            log.info("JWT 토큰이 잘못되었습니다.");
            throw new AppException(ExceptionCode.ERROR_JWT);
        } catch (NullPointerException e){

            log.info("토큰이 헤더에 없습니다.");
            throw new AppException(ExceptionCode.NOT_HEADER_JWT);
        }
    }

    // public String getUsername(){
    //     String token = this.getHeaderToken();

    //     return this.getSubject(token);
    // }

    public String getHeaderToken(){

        String bearerToken = (String)request.getHeader(AuthConstants.AUTH_HEADER.getValue());

        String resultToken = null;

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(AuthConstants.TOKEN_TYPE.getValue())){
            resultToken = bearerToken.substring(7);
        }

        return resultToken;
    }
}
