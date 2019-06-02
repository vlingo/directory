// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.directory.model.message;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import io.vlingo.wire.message.Message;
import io.vlingo.wire.message.MessagePartsBuilder;
import io.vlingo.wire.node.Address;
import io.vlingo.wire.node.AddressType;
import io.vlingo.wire.node.Name;

public class ServiceRegistered implements Message {
  public static final String TypeName = "SRVCREGD";

  public final Set<Address> addresses;
  public final Name name;

  public static ServiceRegistered from(final String content) {
    if (content.startsWith(TypeName)) {
      final Name name = MessagePartsBuilder.nameFrom(content);
      final AddressType type = AddressType.MAIN;
      final Set<Address> addresses = MessagePartsBuilder.addressesFromRecord(content, type);
      return new ServiceRegistered(name, addresses);
    }
    return new ServiceRegistered(Name.NO_NODE_NAME);
  }

  public static ServiceRegistered as(final Name name, final Address address) {
    return new ServiceRegistered(name, address);
  }

  public static ServiceRegistered as(final Name name, final Collection<Address> addresses) {
    return new ServiceRegistered(name, addresses);
  }

  public ServiceRegistered(final Name name, final Address address) {
    this(name);
    this.addresses.add(address);
  }

  public ServiceRegistered(final Name name, final Collection<Address> addresses) {
    this(name);
    this.addresses.addAll(addresses);
  }

  public boolean isValid() {
    return !name.hasNoName();
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    final AddressType type = AddressType.MAIN;

    builder.append(TypeName).append("\n").append("nm=").append(name.value());

    for (final Address address : addresses) {
      builder.append("\n").append(type.field()).append(address.host().name()).append(":").append(address.port());
    }

    return builder.toString();
  }

  private ServiceRegistered(final Name name) {
    this.name = name;
    this.addresses = new HashSet<>();
  }
}
