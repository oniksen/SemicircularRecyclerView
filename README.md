# Semicircular recycler view

![screenshot_20220813_003913 yxZ4c](https://user-images.githubusercontent.com/47987147/184454290-08099438-2033-4f21-9fba-ae5bfaab54fe.png)
<br>
## Description

To implement such a recycler view, you need to create your own implementation of layout manager. Using this library, all you have to do is initialize the *LayoutManager* property of your *RecyclerView* as follows:
```
binding.recyclerView.apply {
    ...
    layoutManager = SemicircularLayoutManager(...)
}
```
You can use different constructors of the **SemicircularLayoutManager** class to display the maximum, or necessary, number of elements in the *RecyclerView*, or set the margins between them.

## Setup

### Step 1. Add the JitPack repository to your settings file.

```
dependencyResolutionManagement {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
### Step 2. Add the dependency.
```
dependencies {
    ...
    implementation 'com.github.Onixen:SemicircularRecyclerView:<library_version>'
}
```
Current library version:<br>
[![](https://jitpack.io/v/Onixen/SemicircularRecyclerView.svg)](https://jitpack.io/#Onixen/SemicircularRecyclerView)


 

