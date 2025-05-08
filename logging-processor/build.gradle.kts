plugins {
    id("java")
}


dependencies {
    implementation("com.squareup:javapoet:1.13.0")
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}