[![](https://jitpack.io/v/HarryJoker/MuilteRecycle.svg)](https://jitpack.io/#HarryJoker/MuilteRecycle)

# MuilteRecycle：多级折叠列表（无限级折叠列表）

MuilteRecycler，使用JSON对象作为列表数据源，只需要配置子列表key及默认的每层展开position，就可以完全实现多级折叠，使用简单且无需进行数据的处理操作！
# Add it in your root build.gradle at the end of repositories:
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

# Add the dependency
dependencies {
	        implementation 'com.github.HarryJoker:MuilteRecycle:1.0.4'
	}

# 示例：
二级列表
