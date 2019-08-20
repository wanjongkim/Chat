package Entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "accounts")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Account.findAll", query = "SELECT a FROM Account a")
    , @NamedQuery(name = "Account.findByUsername", query = "SELECT a FROM Account a WHERE a.accountPK.username = :username")
    , @NamedQuery(name = "Account.findAccount", query = "SELECT a FROM Account a WHERE a.accountPK.username = :username AND a.password = :password")
})
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected AccountPK accountPK;
    @Basic(optional = false)
    @Column(name = "Password")
    private String password;

    public Account() {
    }

    public Account(AccountPK accountPK) {
        this.accountPK = accountPK;
    }

    public Account(AccountPK accountPK, String password) {
        this.accountPK = accountPK;
        this.password = password;
    }

    public Account(String username) {
        this.accountPK = new AccountPK(username);
    }

    public AccountPK getAccountPK() {
        return accountPK;
    }

    public void setAccountPK(AccountPK accountPK) {
        this.accountPK = accountPK;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (accountPK != null ? accountPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Account)) {
            return false;
        }
        Account other = (Account) object;
        if ((this.accountPK == null && other.accountPK != null) || (this.accountPK != null && !this.accountPK.equals(other.accountPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "chat.Account[ accountPK=" + accountPK + " ]";
    }
    
}
