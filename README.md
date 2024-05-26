# Assignment 2
## Restful API development with Spring Webflux

### 目录

1. 完成情况
2. 系统整体设计
    1. 用例图
    2. 接口设计
        - 默认环境
        - UserHandler
        - ItemHandler
    3. 数据库设计
3. 测试截图
    1. JUnit WebTestClient
        - 单元测试
        - 集成测试
    2. Postman
4. 系统截图&功能实现
    - 管理员
    - 普通用户
    - 会话控制
    - 日志
    - 缓存
    - 速率限制
    - OpenAPI文档
    - Spring WebFlux API 实现

---

### 1. 完成情况

**Basic Requirements:**

- Follow the Restful APIs defined in Assignment 1.
- Implement the API in Java with Spring WebFlux API.
- Implement the reactive service layer and dao layer.
- Testing with boot testing framework and WebClient/WebTestClient API.
- API authentication using spring security and JWT will be given credit.
- Using functional api instead of annotation will be given credit.

**Credit Implementations:**

- Caching
- Session Control
- Log
- Rate Limiting

本次作业我们采用Java语言结合Spring WebFlux和Spring Boot框架，确保系统高效运行和易于维护。响应式编程的引入提高了系统的并发处理能力，增强了系统的扩展性和可靠性。

在数据仓库层面，我们选择了MySQL关系型数据库管理系统来存储和管理数据，通过Spring Data R2DBC实现数据访问的响应式编程，保证了数据操作的高效性。

在实现过程中，我们严格遵循了响应式编程的原则：
- 在响应式服务层上：服务层负责处理业务逻辑，并调用数据访问层进行数据存取操作。我们使用Spring WebFlux框架实现了响应式服务层，确保在处理高并发请求时，系统能够保持高效运行。
- 在响应式数据访问层上：数据访问层使用Spring Data R2DBC和ReactiveCrudRepository接口，提供对数据库的响应式访问。通过这种方式，数据访问操作能够在非阻塞的情况下执行，进一步提高了系统的性能。

在测试方面，我们利用Spring Boot的测试框架（JUnit）进行了全面的单元测试和集成测试，并使用WebTestClient API进行了Web API的自动化测试。通过这些测试，我们确保了API的可靠性和稳定性。此外，我们还使用Postman对API进行了调试和验证。

对于API认证与安全，我们使用Spring Security和JWT进行用户鉴权。用户通过登录获得JWT令牌，系统根据用户角色（USER或ADMIN）控制其权限，确保API的安全性和可靠性。

此外，为了提升系统性能和用户体验，结合上课所学内容，我们还增加了一些高级功能，如使用Redis进行数据缓存，提高了系统性能；使用JSON Web Token（JWT）实现会话控制功能，包括认证身份令牌、会话过期验证等；增加日志功能，使用SLF4J（Simple Logging Facade for Java）对系统内数据的增删改查等一切操作进行记录，在控制台输出操作信息并存储到本地文件夹；增加每个用户对API的访问频率限制，使用 Bucket4j 库实现的速率限制（Rate Limiting）拦截器，用于本系统的应用中。这些额外功能实现不仅提高了API的响应速度和并发处理能力，也增强了系统的安全性和可监控性，同时也加深了我们对课上所学知识的理解。

### 2. 系统整体设计

#### 2.1 用例图

本仓库管理系统满足两类用户的需求：管理员和普通用户。
系统允许用户登录，并在登录过程中遇到问题时显示错误信息。登录后，用户可以浏览物品列表以获得库存概览，并可以查看单个物品的详细信息。此外，管理员可以更新物品详情，以反映库存变动或信息更正。如果有必要，管理员还可以删除物品记录。为了库存的扩充，管理员也能够创建新的物品记录。
管理员在拥有普通用户所有功能的基础上，还具有用户管理的特权，包括添加新用户、修改现有用户权限和删除用户等功能。这些功能使管理员能够保持系统用户的有效管理，确保每个用户都能够访问对应的系统功能。通过这样的系统设计，管理员和用户能够确保库存数据的准确性，满足日常的仓库管理需求。

#### 2.2 接口设计

**默认环境**

| 参数名 | 字段值 |
|-------|--------|
| baseUrl | http://localhost:8080 |

##### 2.2.1 UserHandler

1. 获取所有用户

    **GET /users**

    **请求:**

    | 方法 | 路径 | 参数 | 描述 |
    |------|------|------|------|
    | GET | /users | 无 | 获取系统中的所有用户列表 |

    **响应体：**

    200 响应数据格式：JSON

    | 参数名称 | 类型 | 默认值 | 不为空 | 描述 |
    |----------|------|---------|---------|------|
    | data     | object |     | FALSE |      |
    | flag     | boolean |     | FALSE |      |
    | msg      | string  |     | FALSE |      |
    | statusCode | int32 | 200 | FALSE | the http code 200、404 and so on |

2. 创建新用户

    **POST /users**

    **请求:**

    | 方法 | 路径 | 参数 | 描述 |
    |------|------|------|------|
    | POST | /users | User 对象 | 在系统中创建一个新用户 |

    **响应体**

    200 响应数据格式：JSON

    | 参数名称 | 类型 | 默认值 | 不为空 | 描述 |
    |----------|------|---------|---------|------|
    | data     | object |     | FALSE |      |
    | flag     | boolean |     | FALSE |      |
    | msg      | string  |     | FALSE |      |
    | statusCode | int32 | 200 | FALSE | the http code 200、404 and so on |

3. 删除用户

    **DELETE /users/{id}**

    **请求:**

    | 方法 | 路径 | 参数 | 描述 |
    |------|------|------|------|
    | DELETE | /users/{id} | id (路径变量) | 删除指定ID的用户 |

    **响应体**

    200 响应数据格式：JSON

    | 参数名称 | 类型 | 默认值 | 不为空 | 描述 |
    |----------|------|---------|---------|------|
    | data     | object |     | FALSE |      |
    | flag     | boolean |     | FALSE |      |
    | msg      | string  |     | FALSE |      |
    | statusCode | int32 | 200 | FALSE | the http code 200、404 and so on |

4. 更新用户信息

    **PUT /users/{id}**

    **请求:**

    | 方法 | 路径 | 参数 | 描述 |
    |------|------|------|------|
    | PUT | /users/{id} | id (路径变量) User 对象 | 更新指定ID的用户信息 |

    **响应体**

    200 响应数据格式：JSON

    | 参数名称 | 类型 | 默认值 | 不为空 | 描述 |
    |----------|------|---------|---------|------|
    | data     | object |     | FALSE |      |
    | flag     | boolean |     | FALSE |      |
    | msg      | string  |     | FALSE |      |
    | statusCode | int32 | 200 | FALSE | the http code 200、404 and so on |

5. 用户登录

    **POST /users/login**

    **请求:**

    | 方法 | 路径 | 参数 | 描述 |
    |------|------|------|------|
    | POST | /users/login | LoginRequest 对象 | 用户登录，验证凭证 |

    **响应体**

    200 响应数据格式：JSON

    | 参数名称 | 类型 | 默认值 | 不为空 | 描述 |
    |----------|------|---------|---------|------|
    | data     | object |     | FALSE |      |
    | flag     | boolean |     | FALSE |      |
    | msg      | string  |     | FALSE |      |
    | statusCode | int32 | 200 | FALSE | the http code 200、404 and so on |

##### 2.2.2 ItemHandler

1. 获取所有商品

    **GET /items**

    **请求:**

    | 方法 | URL | 参数 | 描述 |
    |------|------|------|------|
    | GET | /items | 无 | 获取所有商品的列表 |

    **响应体**

    200 响应数据格式：JSON

    | 参数名称 | 类型 | 默认值 | 不为空 | 描述 |
    |----------|------|---------|---------|------|
    | data     | object |     | FALSE |      |
    | flag     | boolean |     | FALSE

 |      |
    | msg      | string  |     | FALSE |      |
    | statusCode | int32 | 200 | FALSE | the http code 200、404 and so on |

2. 创建新商品

    **POST /items**

    **请求:**

    | 方法 | URL | 参数 | 描述 |
    |------|------|------|------|
    | POST | /items | Item 对象 | 创建新的商品记录 |

    **响应体**

    200 响应数据格式：JSON

    | 参数名称 | 类型 | 默认值 | 不为空 | 描述 |
    |----------|------|---------|---------|------|
    | data     | object |     | FALSE |      |
    | flag     | boolean |     | FALSE |      |
    | msg      | string  |     | FALSE |      |
    | statusCode | int32 | 200 | FALSE | the http code 200、404 and so on |

3. 删除商品

    **DELETE /items/{id}**

    **请求:**

    | 方法 | URL | 参数 | 描述 |
    |------|------|------|------|
    | DELETE | /items/{id} | id (路径变量) | 删除指定ID的商品记录 |

    **响应体**

    200 响应数据格式：JSON

    | 参数名称 | 类型 | 默认值 | 不为空 | 描述 |
    |----------|------|---------|---------|------|
    | data     | object |     | FALSE |      |
    | flag     | boolean |     | FALSE |      |
    | msg      | string  |     | FALSE |      |
    | statusCode | int32 | 200 | FALSE | the http code 200、404 and so on |

4. 更新商品信息

    **PUT /items/{id}**

    **请求:**

    | 方法 | URL | 参数 | 描述 |
    |------|------|------|------|
    | PUT | /items/{id} | id (路径变量) Item 对象 | 更新指定ID的商品信息 |

    **响应体**

    200 响应数据格式：JSON

    | 参数名称 | 类型 | 默认值 | 不为空 | 描述 |
    |----------|------|---------|---------|------|
    | data     | object |     | FALSE |      |
    | flag     | boolean |     | FALSE |      |
    | msg      | string  |     | FALSE |      |
    | statusCode | int32 | 200 | FALSE | the http code 200、404 and so on |

5. 根据ID获取商品

    **GET /items/{id}**

    **请求:**

    | 方法 | URL | 参数 | 描述 |
    |------|------|------|------|
    | GET | /items/{id} | id (路径变量) | 获取指定ID的商品详细信息 |

    **响应体**

    200 响应数据格式：JSON

    | 参数名称 | 类型 | 默认值 | 不为空 | 描述 |
    |----------|------|---------|---------|------|
    | data     | object |     | FALSE |      |
    | flag     | boolean |     | FALSE |      |
    | msg      | string  |     | FALSE |      |
    | statusCode | int32 | 200 | FALSE | the http code 200、404 and so on |

#### 2.3 数据库设计

**（1）物理模型：**

**（2）逻辑模型：**

---

### 3. 测试截图

#### 3.1 JUnit WebTestClient

**3.1.1 单元测试**

ItemServiceTest：测试用例全部通过

UserServiceTest：测试用例全部通过

**3.1.2 集成测试**

ItemHandlerTest：测试用例全部通过

UserHandlerTest：测试用例全部通过

#### 3.2 Postman

**(1) 登录功能**

登录成功：

登录失败：

**(2) 查看物品清单**

登录成功并获得token后，进行查看物品清单：

没有获得token时获取不到信息：

**(3) 测试增加物品**

**(4) 测试删除刚刚添加的信息**

**(5) 测试修改某条数据信息**

**(6) 查找某条具体信息**

**(7) 权限测试**

该系统为普通用户和管理员用户设置了不同的权限，增删改功能均只对管理员开放，查找所有和单个物品信息则开放给全体用户，如果普通用户尝试进行增删改操作，则会被系统拒绝：

但是可以进行查看信息：

**(8) 测试速率限制**

当请求超过一定次数时，会限制访问

---

### 4. 系统截图&功能实现

#### 4.1 管理员

**（1）管理员登录：**

**（2）管理员界面：（可以进行增删改）**

**（3）增加商品：**

**（4）查看单个信息：**

**（5）更改单个信息：**

#### 4.2 普通用户

**（1）普通用户登录：**

**（2）只能查看信息：**

#### 4.3 会话控制

当用户在设定的token有效期内没有操作时，系统会提示用户重新登录（会话过期）

#### 4.4 日志

系统设置了Log来记录对数据的操作，将所有信息输出到控制台中同时保存到log日志文件内包括：访问时间，对访问接口的记录，登陆用户的信息、权限记录，访问接口的返回结果

#### 4.5 缓存

当用户首次登录时，会从数据库中获取所有物品信息，并将allItems存在缓存中；当查看某一物品信息时，会将该物品的查看信息(item:id)存在缓存；当删除某一物品信息时，会删除缓存中的allItems和其对应的item:id；当创建物品时，会删除缓存中的allItems并讲该物品信息(item:id)存在缓存；当更新某一物品信息时，会删除allItems同时更新缓存中对应的的item:id；当用户切换账号登录时，缓存内容会被清空

#### 4.6 速率限制

使用拦截器并利用Bucket4j库实现速率限制，当对某一接口的请求超过一定次数时，会限制访问

#### 4.7 OpenAPI文档

**（1） Item API**

**（2） User API**

#### 4.8 Spring WebFlux API 实现 

**RouterConfig：** 该类配置了所有的路由，定义了应用程序中各个API的路径和处理方法。

**ItemHandler：** 处理与物品相关的请求，包括获取所有物品、根据ID获取物品、创建物品、更新物品和删除物品。
- 使用 @PreAuthorize 注解来限制访问权限
- 使用 Mono 和 flatMap 来实现异步和非阻塞操作
- ReactiveRedisOperations 用于与Redis进行异步操作，缓存物品数据

**UserHandler：** 处理与用户相关的请求，包括获取用户列表、创建用户、删除用户和用户登录
- JwtUtils 用于生成和验证JWT令牌
