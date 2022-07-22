package cd.clavatar.wani.vendor;

/**
 * Created by Cl@v@t@r on 2020-01-29
 */
public enum RoleEnum {

    CONTROLER("Controler"),
    AGENT("Agent"),
    SUPERVISER("Superviser"),
    ADMIN("Admin"),
    SUPER_ADMIN("Super admi");

    private String val;

    private RoleEnum(String val){ this.val = val;}

    public String getValue(){return this.val;}
}
