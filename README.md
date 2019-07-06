# AopClearLog

Clear sensitive logs using AST and APT. 

利用 AST 语法树实现 AOP 思想，本项目抛砖引玉，用 AST 达到清除Log的目的，如果你能熟练掌握 AST 这一利器，就可以任意地 AOP 啦！

## 一、背景 —— 为什么我要用AST来实现AOP思想？

Aspect 语法难懂？ASM 字节码操作繁琐？APT 难以精准找到切入点？你该试试 AST 了！编辑器级别，效率高，更轻量。

## 二、通过本项目你能学到什么技术点？

- APT注解处理器
- AST抽象语法树
- 编译原理

## 三、如何使用

clone 本项目，运行后观察 logcat 无 MainActivity 中的 log 输出，即为清除成功。

## 四、原理

既然要操作 AST，我们怎么拿到 AST 呢？

答案是：在注解处理器 APT！

利用 JDK 的注解处理器，可在编译期间处理注解，还可以读取、修改、添加 AST 中的任意元素，让改动后的 AST 重新参与编译流程处理，直到语法树没有改动为止。

![](https://upload-images.jianshu.io/upload_images/3167794-8a6c67f65e124ae4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/908/format/webp)

## 五、实践步骤

整体思路：在编译期间拿到 AST，扫描是否含有特定日志语句如：Log，存在则删除该语句。

1. 实现 AbstractProcessor

![](https://upload-images.jianshu.io/upload_images/3167794-1b2eee38291ea305.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/584/format/webp)

2. 添加注解
@SupportedAnnotationTypes 指定此注解处理器支持的注解，可用 * 指定所有注解
@SupportedSourceVersion 指定支持的java的版本

![](https://upload-images.jianshu.io/upload_images/3167794-57d051947d6cee74.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/572/format/webp)

3. 获取 AST

![](https://upload-images.jianshu.io/upload_images/3167794-348bf2870dcc022d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/745/format/webp)

在注解处理器的 init 函数里，通过 Trees.instance(env) 拿到抽象语法树（AST）。
此处把ProcessingEnvironment强转成JavacProcessingEnvironment，后面的操作都变成了IDE编辑器内部的操作了。

4. 操作 AST

![](https://upload-images.jianshu.io/upload_images/3167794-cb5f1c5016c02400.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/990/format/webp)

在注解处理器的 process 函数中，我们扫描所有的类，实现一个自定义的 TreeTranslator。

![](https://upload-images.jianshu.io/upload_images/3167794-cc8d45a1c8d52023.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/804/format/webp)

为什么自定义的 TreeTranslator 要复写 visitBlock？因为我们的需求场景是扫描所有 log 语句，粒度为语句块。AST 支持我们以不同的粒度去访问，还有哪些粒度呢？我们看下TreeTranslator 的继承层次，可以发现一个 Visitor 类。

![](https://upload-images.jianshu.io/upload_images/3167794-1580e33c2e8e2b54.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1000/format/webp)

打开 Visitor 类：

![](https://upload-images.jianshu.io/upload_images/3167794-a1f5e6c041579a7e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/925/format/webp)

所有 visit 方法一目了然，我们前面提到 AST 每一个节点都代表着源语言中的一个语法结构，所以我们可以细粒度到指定访问 if、return、try等特定类型节点，只需覆写相应的 visit 方法。

回到我们的需求场景：扫描所有 log 语句，既然是语句，粒度应该为语句块，所以我们覆写 visitBlock 进行扫描，当扫描到指定语句比如 Log. 时，就不把整个语句都写入 AST，以此达到清除 log 语句的效果。

![](https://upload-images.jianshu.io/upload_images/3167794-f0220b58fe00bb35.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/997/format/webp)


> 详细介绍可见我的博文：[AOP 最后一块拼图 | AST 抽象语法树 —— 最轻量级的AOP方法](https://www.jianshu.com/p/0f1c7b3e907f)
