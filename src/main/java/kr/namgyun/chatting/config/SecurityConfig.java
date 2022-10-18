package kr.namgyun.chatting.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    /**
     * 아이디 비밀번호 임의로 설정
     * @param auth the {@link AuthenticationManagerBuilder} to use
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        아이디에 대한 패스워드 유형 prefix 패스워드 암호화 방식을 앞에 붙여줘야함 (noop 암호화 안한다는거)
        auth.inMemoryAuthentication().withUser("user").password("{noop}1111").roles("USER");
        auth.inMemoryAuthentication().withUser("sys").password("{noop}1111").roles("SYS","USER");
        auth.inMemoryAuthentication().withUser("admin").password("{noop}1111").roles("ADMIN","SYS","USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        인가 정책


        /**
         * 인가 정책 위에 줄 부터 실행되기 때문에 admin/pay의 경우 위에서 ADMIN이 아니면 바로 막힘
         */
        http.authorizeRequests()//요청에 대한 권한을 지정
                .antMatchers("/**").permitAll()
                /**
                 * anyRequest().authenticated() 때문에 이상한페이지 눌러도 login 페이지로 가짐
                 * 로그인해있지만 권한이 없는 인가 문제면 denied페이지로 가짐
                 */
                .anyRequest().authenticated()  //인가 정책을 통과해 인증이 되어야 접근가능
                .and()                      //and를 해야 어나니머스가 보임
                .anonymous().disable()      //익명사용자 사용 안함
        ;

//        인증 정책
        http.formLogin()    //폼 로그인 방식으로
//                .loginPage("/login")        //사용자 정의 로그인 페이지
//                .defaultSuccessUrl("/")         //로그인 성공 후 이동 페이지
//                .failureUrl("/login")           //로그인 실패 후 이동 페이지
                .usernameParameter("user_id")    //파라메터 아이디 설정
                .passwordParameter("user_pw")    //파라메터 비밀번호 설정
//                .loginProcessingUrl("login_proc")   //로그인 폼 액션 url
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        log.info("로그인 성공 아이디는 :"+authentication.getName());
                        /**
                         * 인증실패해서 로그인 페이지 가서 로그인 했을때 원래 가고자 했던곳 보내기
                         * 인증가 정책에 의해서 권한이 필요한곳에 갔을때만 돼고 바로 인증필요없는 login으로가면
                         * savedRequest에는 null이 담겨있게됨
                         */
                        RequestCache requestCache = new HttpSessionRequestCache();
                        SavedRequest savedRequest = requestCache.getRequest(request,response);
                        /**
                         * 세션에 저장돼 있던 사용자가 원래 가고자 했던 경로
                         */
                        String redirectUrl="";
                        if (savedRequest ==null){
                            redirectUrl="/";
                        }else {
                            redirectUrl=savedRequest.getRedirectUrl();
                        }
                        response.sendRedirect(redirectUrl);

                    }
                })
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                        log.info("로그인 실패 :"+exception.getMessage());
                        response.sendRedirect("/login");
                    }
                })
                .permitAll()
        ;
//        로그아웃

        http.logout()
                .logoutUrl("/logout")
//                .logoutSuccessUrl("/login")
                .addLogoutHandler(new LogoutHandler() {
                    @Override
                    public void logout(HttpServletRequest request,
                                       HttpServletResponse response, Authentication authentication) {
                        HttpSession httpSession =request.getSession();
                        log.info("세션 무효화");
                        httpSession.invalidate();           //세션 무효화
                    }
                })
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                        log.info("로그아웃 성공");
                        response.sendRedirect("/login");
                    }
                })
//                .deleteCookies("remember-me")           //remember-me 라는 쿠키를 삭제
        ;
        /**
         * 자동로그인 리멤버 미
         */
        http.rememberMe()
                .rememberMeParameter("remember")    //기본 파라메터 명은 remember-me
                .tokenValiditySeconds(3600)         //디폴트는 14일
                .alwaysRemember(false)              //true로 리멤버 미 기능이 활성화되지 않아도 항상 실행
                .userDetailsService(userDetailsService)   //리멤버 미 기능을 수행할때 시스템에 있는 사용자 계정을 조회할때씀
                ;
        /**
         * 세션 관리
         */
        http.sessionManagement()
                .maximumSessions(1)     //동시 생성가능 세션 수 -1은 무제한
//                동시 로그인 차단하게되면 로그아웃할때 세션 무효화지 삭제가 아니라
//                세션이 무효화 되었어도 사라지지 않아서 세션1개 있다고 로그인 안됨
//                false 로하면 로그인돼서 걍 false 로
                .maxSessionsPreventsLogin(false)     //동시 로그인 차단(true), false 가 디폴트+이전 세션 만료
                .expiredUrl("/expired")             //세션이 만료된 경우 이동 할 페이지
                .and()                              //이걸 해야 invalid뜸
                .invalidSessionUrl("/login")      //세션이 유효하지 않을 때 이동 할 페이지 로그인했는데 또 로그인 시도하면 가게 되는 경로라고봄
//                changeSessionId는 3.1이상 버전 migrateSession은 3.1이하버전 디폴트
                .sessionFixation().changeSessionId()    //기본값 인증을 성공하게 되면 사용자 세션은 그대로 두고 세션 아이디만 변경
//                .sessionFixation().migrateSession()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)   //디폴트 스크링 시큐리티가 필요시 사용, Always 스프링 시큐리티가 항상
                ;

        /**
         * 인증 인가 예외 처리
         * 위의 로그인 석세스 핸들러랑 같이봐야됨
         */
        http
                .exceptionHandling()
                //인증 예외
                /**
                 * 이거넣으면 logout이나 login 시큐리티 기본껄로 안가짐
                 * 왜 무한루프가 되는지 모르겠음
                 */
/*                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//                        스프링 시큐리티의 login이 아닌 우리가 직접 만든 login 페이지로 감
                        log.info("앤트리포인트 발생");
                        response.sendRedirect("/");
                    }
                })*/
//                인가 예외
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        log.info("액세스 디나이 핸들러 발생");
                        response.sendRedirect("/denied");
                    }
                });
        /**
         * csrf필터 비활성화
         */
        http.csrf().disable();
    }

}
