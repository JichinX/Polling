# Polling

> 更加灵活的后台任务轮询基础框架，kotlin编写

### 所需依赖

1. `implementation "androidx.lifecycle:lifecycle-process:2.3.0-beta01"`

**lifecycle-process**是lifecycle组件库下的一扩展库，主要是基于Lifecycle的特性和`registerActivityLifecycleCallbacks`方法来实现对APP大局上的生命周期进行外发，但是无法处理App退出时的一些操作，简单来说，**lifecycle-process**可以暴漏APP的前台、后台、息屏状态下的生命周期，

在**Polling**库内，模仿其实现，并新增对APP退出后的生命周期外发，

   2.`implementation 'androidx.core:core-ktx:1.3.2'`

**core-ktx**是基于kotlin对**core**的功能增强，

在**Polling**库中，使用其对SpaseArray的扩展方法**foreach**

### 如何使用

1.**添加依赖**(暂时为SNAPSHOT状态)

```kotlin
 implementation("me.xujichang.lib:polling:1.0.0-20201102.093218-5")
 //或
 implementation("me.xujichang.lib:polling:1.0.0-SNAPSHOT")
```

2.**初始化**(使用时无需此步骤，此处仅做原理说明)

初始化借助**ContentProvider**，原因有三：

1.   获取Context,

2. 其初始化在Application之前

3. 无需手动初始化，减少使用时出错几率

具体如下：

```xml
 <provider
       android:name="me.xujichang.lib.provider.AppLifecycleOwnerInitializer"
       android:authorities="${applicationId}.app-lifecycle"
       android:exported="false"
       android:multiprocess="true" />
```

关键代码：

```kotlin
override fun onCreate(): Boolean {
    context?.let {
    AppLifecycleDispatcher.init(it)
    AppLifecycleOwner.init(it)
        }
   return true
}
```

3.**使用**

3.1 基本方法：

3.1.1**任务(Job)的包装类

任务目前按照唯一标识的类型分为三类

**TagJob**:以Tag字符串作为唯一标识符

**IdsJob**：以id数字类型作为唯一标识符

**CleanJob**：无唯一标识符

其定义如下：

```kotlin
open class JobWrapper(lifecycleOwner: LifecycleOwner, val job: BaseJob) :
    LifecycleEventObserver {
    init {
        lifecycleOwner.lifecycle.addObserver(getObserver())
    }

    private fun getObserver(): LifecycleObserver = this
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            JobPool.removeJob(this)
        }
    }
}

class IdsJob(lifecycleOwner: LifecycleOwner, job: BaseJob, val id: Long) :
    JobWrapper(lifecycleOwner, job)

class CleanJob(lifecycleOwner: LifecycleOwner, job: BaseJob) :
    JobWrapper(lifecycleOwner, job)


class TagJob(lifecycleOwner: LifecycleOwner, job: BaseJob, val tag: String) :
    JobWrapper(lifecycleOwner, job)行
```

以上可知，构造函数有：提供生命周期的对象lifecycleOwner， 任务job,以及唯一标识符

3.1.2 **任务创建**

目前**任务**已经归于抽象于一个类中，通过参数修改其适用的轮询机制

 主要方法如下：

 **构造方法**

```kotlin
class JobWithLifecycle(
    private val lifecycleOwner: LifecycleOwner,
    interval: Int = DEFAULT_INTERVAL,
    private val workState: Lifecycle.State = Lifecycle.State.RESUMED,
    private val updateInterceptor: (() -> Boolean) = { true },
    private val runFunc: () -> Unit,
) :
    BaseJob(interval),
    LifecycleEventObserver {
//...
}
```

其中：**lifecycleOwner**依然是提供生命周期的对象

            **interval**轮询周期，以秒(s)为单位

            **workState**任务可执行时，生命周期的状态 ，具体参考`Lifecycle.State`

            **updateInterceptor**在可执行的生命周期内，增加自定义的条件限制，此为kotlin函数对象

            **runFunc**执行时的函数，通过参数暴漏出去，实现自己的任务具体内容

3.2 **Polling**目前支持三种轮询机制，以下实例说明

3.2.1Activity/Fragment页面内的轮询，功能页面退出后后，轮询停止

```kotlin
class PollingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
 JobPool.add(IdsJob(this, JobWithLifecycle(this) {
            //...
        }, 1000))
  JobPool.add(TagJob(this, JobWithLifecycle(this) {
            //...
        }, "1000"))
  JobPool.add(CleanJob(this, JobWithLifecycle(this) {
            //...
        }))
 }
}
```

3.2.2 App运行期间运行的轮询之受息屏、退出后台等影响,即息屏后停止，App被切换后台会停止，关键在于借助**ProcessLifecycleOwner**，

```kotlin
  JobPool.add(
            TagJob(
                ProcessLifecycleOwner.get(),
                JobWithLifecycle(
                    ProcessLifecycleOwner.get(),
                    interval = 3,
                    workState = Lifecycle.State.RESUMED
                ) {
                    //...
                },
                "App-1",
            )
        )
  //..其他类型Job类似
```

3.2.3 App运行期间不间断进行轮询，进程不被取消，则轮询不止

主要借助**AppLifecycleOwner**，仿照**ProcessLifecycleOwner**并扩展部分方法

```kotlin
 JobPool.add(
            CleanJob(
                AppLifecycleOwner.get(),
                JobWithLifecycle(
                    AppLifecycleOwner.get(),
                    workState = Lifecycle.State.CREATED,
                   ) {
                    //...
                }
            )
        )
```

3.3 其他使用场景

**APP退出后，停止轮询**

注意是：app退出，不是app进入后台，此处app退出的判断依据是：所有的activity都已关闭

```kotlin
 JobPool.add(
            CleanJob(
                AppLifecycleOwner.get(),
                JobWithLifecycle(
                    AppLifecycleOwner.get(),
                    workState = Lifecycle.State.CREATED,
                    updateInterceptor = {
                        !AppLifecycleOwner.isDestroyed()
                    }) {
                    //...
                }
            )
        )
```

3.4 其他注意事项：

1，APP运行期间，由app启动的service优先级还是比较高的，所以暂时不考虑，程序运行期间以及息屏时，系统对后台service的强制关闭，

2，由于此框架的service 和任务Job是完全隔离的，所以可以实现自己的service，比如高优先级的前台service等，配合任务job,灵活的实现后台轮询功能

3，若项目确实有需要使用双进程保活进行后台轮询来哦执行任务的话，可以配合**heartbeat**使用
