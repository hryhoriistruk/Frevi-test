package HireCraft.com.SpringBoot.dtos.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientBookingViewResponse {
    private Long id;
    private String providerFullName;
    private String occupation;

    private String city;
    private String state;
    private String country;
    private String timeAgo;
    private String status;

    public void setId(Long id) {
        this.id = id;
    }

    public void setProviderFullName(String providerFullName) {
        this.providerFullName = providerFullName;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
