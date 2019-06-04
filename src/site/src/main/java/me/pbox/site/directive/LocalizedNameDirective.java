/*
 * Copyright by Mike Mirzayanov
 */
package me.pbox.site.directive;

import com.codeforces.commons.text.Patterns;
import com.codeforces.commons.text.StringUtil;
import freemarker.core.Environment;
import freemarker.template.*;
import freemarker.template.utility.DeepUnwrap;
import me.pbox.site.model.Localized;
import me.pbox.site.util.LocaleUtil;
import org.apache.commons.text.StringEscapeUtils;
import org.nocturne.main.ApplicationContext;

import java.io.IOException;
import java.util.Map;

/**
 * Use it to get well-formatted, colored link to team.
 *
 * @author Andrew Lazarev
 */
public class LocalizedNameDirective implements TemplateDirectiveModel {
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException {
        if (!params.containsKey("of")) {
            throw new TemplateModelException("LocalizedNameDirective directive expects 'of'.");
        }

        if (params.size() != 1) {
            throw new TemplateModelException("TeamDirective directive expects the only parameter named 'of'.");
        }

        Object obj = DeepUnwrap.unwrap((TemplateModel) params.get("of"));

        if (obj instanceof Localized) {
            Localized localized = (Localized) obj;
            String name;

            if (LocaleUtil.isRussian(ApplicationContext.getInstance().getLocale())) {
                name = StringUtil.isEmpty(localized.getRussianName())
                        ? localized.getEnglishName()
                        : localized.getRussianName();
            } else {
                name = StringUtil.isEmpty(localized.getEnglishName())
                        ? localized.getRussianName()
                        : localized.getEnglishName();
            }

            name = StringEscapeUtils.escapeHtml4(name);

            name = Patterns.LINE_BREAK_PATTERN.matcher(name).replaceAll("<br/>");

            env.getOut().write(name);
        }
    }
}
