package org.backend.user.repository.interfaces;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface HibernateJPAInterface<T> {
    //The findAll method will trigger an UnsupportedOperationException
    <S extends T> S persist(S entity);
    <S extends T> S persistAndFlush(S entity);
    <S extends T> List<S> persistAll(Iterable<S> entities);
    <S extends T> List<S> persistAllAndFlush(Iterable<S> entities);
    <S extends T> S merge(S entity);
    <S extends T> S mergeAndFlush(S entity);
    <S extends T> List<S> mergeAll(Iterable<S> entities);
    <S extends T> List<S> mergeAllAndFlush(Iterable<S> entities);
    <S extends T> S update(S entity);
    <S extends T> S updateAndFlush(S entity);
    <S extends T> List<S> updateAll(Iterable<S> entities);
    <S extends T> List<S> updateAllAndFlush(Iterable<S> entities);
}
