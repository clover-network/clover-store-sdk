### add permission 
```html
    <uses-permission android:name="android.permission.INTERNET" />
```

### add network config

- application root add networkSecurityConfig
```html
  android:networkSecurityConfig="@xml/network_security_config"
```
- network_security_config content
```html
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true" >
    </base-config>
</network-security-config>

```

### add extra lib

in root build.gradle

```html
buildscript {
    ext.kotlin_version = "1.4.10"
    repositories {
        google()
        jcenter()
        maven { url "https://jitpack.io" }
    }
    dependencies {
        classpath 'com.yanzhenjie.andserver:plugin:2.1.6'   // add this lib
    }
}
```

### add clover in your entry activity

```html
 CloverOS.initOS(this.applicationContext,this,webview)
```
