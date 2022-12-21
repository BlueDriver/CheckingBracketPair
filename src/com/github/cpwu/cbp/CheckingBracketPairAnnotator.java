package com.github.cpwu.cbp;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Stack;

/**
 * Checking Bracket Pair
 * com.github.cpwu.cbp
 *
 * @author wcp
 * @since 2022/12/16 19:46 Friday
 */
public class CheckingBracketPairAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder holder) {
        this.checkBracketsMatch(psiElement, holder);
    }

    /**
     * 校验字符中 [] {} 是否匹配
     * 括号匹配算法：
     * 栈的典型运用场景，若栈空，则括号入栈，其后的括号和栈顶进行比较，若匹配，则栈顶元素出栈，否则入栈，直至结束，若栈不空，则不匹配
     */
    private void checkBracketsMatch(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!PsiUtil.isJavaToken(element, JavaTokenType.STRING_LITERAL)) {
            return;
        }
        String value = element.getText();// value带有双引号
        String string = value.replaceAll("\"", "");

        if (string.contains("[") || string.contains("]") || string.contains("{") || string.contains("}")) {

            Stack<Character> stack = new Stack<>();

            for (char c : string.toCharArray()) {
                if ('[' == c) {
                    stack.push(c);
                } else if (']' == c) {
                    if (stack.isEmpty()) {
                        stack.push(c);
                    } else {
                        if (stack.peek() == '[') {
                            stack.pop();
                        } else {
                            stack.push(c);
                        }
                    }
                } else if ('{' == c) {
                    stack.push(c);
                } else if ('}' == c) {
                    if (stack.isEmpty()) {
                        stack.push(c);
                    } else {
                        if (stack.peek() == '{') {
                            stack.pop();
                        } else {
                            stack.push(c);
                        }
                    }
                }
            }

            if (stack.isEmpty()) {
                return;
            }

            TextRange range = new TextRange(element.getTextRange().getStartOffset(),
                    element.getTextRange().getEndOffset());
            // 黄色波浪线
            holder.createWeakWarningAnnotation(range, "Unmatched Bracket：" + stack.peek());

        }

    }


}
