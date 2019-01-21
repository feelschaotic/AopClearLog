package com.example.processor;

import com.google.auto.service.AutoService;
import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;


@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("*")
public class ClearLogProcessor extends AbstractProcessor {
    private Trees mTrees;
    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        mMessager = env.getMessager();
        if (env instanceof JavacProcessingEnvironment) {
            mTrees = Trees.instance((JavacProcessingEnvironment) env);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (!roundEnvironment.processingOver() && mTrees != null) {
            roundEnvironment.getRootElements().stream()
                    .filter(it -> it.getKind() == ElementKind.CLASS)
                    .forEach(it -> ((JCTree) mTrees.getTree(it)).accept(new LogClearTranslator()));
        }
        return false;
    }

    class LogClearTranslator extends TreeTranslator {

        public static final String LOG_TAG = "Log.";

        @Override
        public void visitBlock(JCTree.JCBlock jcBlock) {
            super.visitBlock(jcBlock);
            List<JCTree.JCStatement> jcStatementList = jcBlock.getStatements();
            if (jcStatementList == null || jcStatementList.isEmpty()) {
                return;
            }
            List<JCTree.JCStatement> out = List.nil();
            for (JCTree.JCStatement jcStatement : jcStatementList) {
                if (isLogStatements(jcStatement)) {
                    mMessager.printMessage(Diagnostic.Kind.WARNING, this.getClass().getCanonicalName() + "LogClear:" + jcStatement.toString());
                } else {
                    out = out.append(jcStatement);
                }  
            }
            jcBlock.stats = out;
        }


        private boolean isLogStatements(JCTree.JCStatement jcStatement) {
            return jcStatement.toString().contains(LOG_TAG);
        }
    }
}

