package ru.otus.crm.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "client")
public class Client implements Cloneable {

    @Id
    @SequenceGenerator(name = "client_gen", sequenceName = "client_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_gen")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER, mappedBy = "client")
    private List<Phone> phones;

    public Client(String name) {
        this.id = null;
        this.name = name;
    }

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Client(Long id, String name, Address address, List<Phone> phones) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phones = phones;
    }

    @Override
    @SuppressWarnings({"java:S2975", "java:S1182"})
    public Client clone() {
        Address clonedAddress = this.address != null ? this.address.clone() : null;
        return new Client(this.id, this.name, clonedAddress, cloneAndLinkPhones(this.phones));
    }

    private List<Phone> cloneAndLinkPhones(List<Phone> originalPhones) {
        if (originalPhones == null) {
            return new ArrayList<>();
        }

        List<Phone> clonedPhones = new ArrayList<>(originalPhones.size());
        for (Phone phone : originalPhones) {
            Phone clonedPhone = phone.clone();
            clonedPhone.setClient(this);
            clonedPhones.add(clonedPhone);
        }
        return clonedPhones;
    }

    @Override
    public String toString() {
        return "Client{" + "id=" + id + ", name='" + name + '\'' + ", address='" + (address != null ? address : "null")
                + '\''
                + ", phones='"
                + (phones != null
                        ? phones.stream()
                                .map(Phone::getNumber)
                                .reduce((x, y) -> String.join(",", x, y))
                                .orElse("")
                        : "null")
                + '\''
                + '}';
    }
}
