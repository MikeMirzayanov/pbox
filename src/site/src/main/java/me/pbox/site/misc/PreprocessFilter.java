package me.pbox.site.misc;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author Maxim Gusarov (gusarov.maxim@gmail.com)
 */
public class PreprocessFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No operations.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // No operations.
    }
}
