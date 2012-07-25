/**
 * data-server. 2012-6-28
 */
package com.anxlng.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * 测试套件的使用
 * 在实际项目中，随着项目进度的开展，单元测试类会越来越多，可是直到现在我们还只会一个一个的单独运行测试类，这在实际项目实践中肯定是不可行的。
 * 为了解决这个问题，JUnit 提供了一种批量运行测试类的方法，叫做测试套件。这样，每次需要验证系统功能正确性时，只执行一个或几个测试套件便可以了。
 * 测试套件的写法非常简单，您只需要遵循以下规则：创建一个空类作为测试套件的入口。
 * 使用注解 org.junit.runner.RunWith 和 org.junit.runners.Suite.SuiteClasses 修饰这个空类。
 * 将 org.junit.runners.Suite 作为参数传入注解 RunWith，以提示 JUnit 为此类使用套件运行器执行。
 * 将需要放入此测试套件的测试类组成数组作为注解 SuiteClasses 的参数。
 * 保证这个空类使用 public 修饰，而且存在公开的不带有任何参数的构造函数
 * 
 * @author tangjixing <anxlng@sina.com>
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(ByteFuncTest.class)
public class StaticUtilTest {

}
