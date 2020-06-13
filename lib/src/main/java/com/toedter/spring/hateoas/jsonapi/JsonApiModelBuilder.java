/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.toedter.spring.hateoas.jsonapi;

import org.springframework.hateoas.*;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Builder API to create complex JSON:API representations exposing a JSON:API idiomatic API.
 * It includes building JSON:API {@literal relationships} and {@literal included}.
 *
 * @author Kai Toedter
 */
public class JsonApiModelBuilder {
    private RepresentationModel<?> model;
    private Links links = Links.NONE;
    private final HashMap<String, List<JsonApiRelationship>> relationships = new HashMap<>();
    private final List<RepresentationModel<?>> included = new ArrayList<>();

    private JsonApiModelBuilder() {
    }

    /**
     * Sets the  {@link RepresentationModel} as the base of
     * the {@literal RepresentationModel} to be built.
     * <p>
     * NOTE: If the model is already set, an {@literal IllegalStateException} will be thrown.
     *
     * @param model must not be {@literal null}.
     * @return will never be {@literal null}.
     */
    public JsonApiModelBuilder model(RepresentationModel<?> model) {

        Assert.notNull(model, "RepresentationModel must not be null!");

        if (this.model != null) {
            throw new IllegalStateException("Model object already set!");
        }

        this.model = model;

        this.links(model.getLinks());

        return this;
    }

    /**
     * Creates an {@link EntityModel} from the {@literal object} as the base of
     * the {@literal RepresentationModel} to be built.
     * <p>
     * NOTE: If the model is already set, an {@literal IllegalStateException} will be thrown.
     *
     * @param object must not be {@literal null}.
     * @return will never be {@literal null}.
     */
    public JsonApiModelBuilder model(Object object) {
        return this.model(EntityModel.of(object));
    }

    /**
     * Adds a {@link Link} to the {@link RepresentationModel} to be built.
     * <p>
     * NOTE: This adds it to the top level.
     * If you need a link inside the model you added with {@link #model(RepresentationModel)} method,
     * add it directly to the model.
     *
     * @param link must not be {@literal null}.
     * @return will never be {@literal null}.
     */
    public JsonApiModelBuilder link(Link link) {
        this.links = links.and(link);
        return this;
    }

    /**
     * Adds a {@link Link} with the given href and {@link LinkRelation}
     * to the {@link RepresentationModel} to be built.
     *
     * @param href     must not be {@literal null}.
     * @param relation must not be {@literal null}.
     * @return will never be {@literal null}.
     */
    public JsonApiModelBuilder link(String href, LinkRelation relation) {
        return link(Link.of(href, relation));
    }

    /**
     * Adds the given {@link Link}s to the {@link RepresentationModel} to be built.
     *
     * @param links must not be {@literal null}.
     * @return will never be {@literal null}.
     */
    public JsonApiModelBuilder links(Iterable<Link> links) {
        this.links = this.links.and(links);

        return this;
    }

    /**
     * Adds the given {@literal relationship} based on the given {@link EntityModel}
     * to the {@link RepresentationModel} to be built.
     *
     * @param name        must not be {@literal null}.
     * @param entityModel must not be {@literal null}.
     * @return will never be {@literal null}.
     */
    public JsonApiModelBuilder relationship(String name, EntityModel<?> entityModel) {
        Assert.notNull(name, "Relationship name must not be null!");
        Assert.notNull(entityModel, "EntityModel must not be null!");

        List<JsonApiRelationship> relationships = this.relationships.computeIfAbsent(name, k -> new ArrayList<>());
        relationships.add(JsonApiRelationship.of(entityModel));
        return this;
    }

    /**
     * Adds the given {@literal relationship} based on the given {@link Object}
     * to the {@link RepresentationModel} to be built. The object is automatically
     * wrapped into an {@link EntityModel}.
     *
     * @param name   must not be {@literal null}.
     * @param object must not be {@literal null}.
     * @return will never be {@literal null}.
     */
    public JsonApiModelBuilder relationship(String name, Object object) {
        return this.relationship(name, EntityModel.of(object));
    }

    /**
     * Adds the given {@link EntityModel}
     * to the {@literal included} {@link EntityModel}s.
     * It will appear then top level {@literal JSON:API included} values.
     *
     * @param entityModel must not be {@literal null}.
     * @return will never be {@literal null}.
     */
    public JsonApiModelBuilder included(EntityModel<?> entityModel) {
        included.add(entityModel);
        return this;
    }

    /**
     * Adds the given {@link Object}
     * to the {@literal included} {@link EntityModel}s.
     * The object is automatically wrapped into an {@link EntityModel}.
     * It will appear then top level {@literal JSON:API included} values.
     *
     * @param object must not be {@literal null}.
     * @return will never be {@literal null}.
     */
    public JsonApiModelBuilder included(Object object) {
        return this.included(EntityModel.of(object));
    }

    /**
     * Transform the entities, Links, relationships and included
     * into a {@link RepresentationModel}.
     *
     * @return will never be {@literal null}.
     */
    public RepresentationModel<?> build() {
        return new JsonApiModel(model, relationships, included, links);
    }

    /**
     * Creates a new {@link JsonApiModelBuilder}.
     *
     * @return will never be {@literal null}.
     */
    public static JsonApiModelBuilder jsonApiModel() {
        return new JsonApiModelBuilder();
    }
}