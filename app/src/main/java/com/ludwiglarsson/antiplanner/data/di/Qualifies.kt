package com.ludwiglarsson.antiplanner.data.di

import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocalRevision


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RemoteRevision


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LimitedDispatcher
