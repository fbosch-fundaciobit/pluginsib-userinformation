package org.fundaciobit.plugins.userinformation;

import java.security.cert.X509Certificate;

import org.fundaciobit.plugins.IPlugin;

/**
 * 
 * @author anadal
 * 
 */
public interface IUserInformationPlugin extends IPlugin {

  public static final String USERINFORMATION_BASE_PROPERTY = IPLUGIN_BASE_PROPERTIES + "userinformation.";

	/**
	 * Mètode que retorna informació de l'usuari amb nif igual al paràmetre.
	 * 
	 * @param nif AdministrationID
	 * @return Si torna null significa que l'usuari no existeix. Si l'usuari
	 *         existeix però no es vol retornar informació del mateix , llavors
	 *         només es requereix que es retorni una instància de
	 *         UserInfo emplenant com a mínim username i nif.
	 */
	public UserInfo getUserInfoByAdministrationID(String administrationID) throws Exception;

	 /**
   * Mètode que retorna informació de l'usuari amb username igual al paràmetre.
   * 
   * @param nif
   * @return Si torna null significa que l'usuari no existeix. Si l'usuari
   *         existeix però no es vol retornar informació del mateix , llavors
   *         només es requereix que es retorni una instància de
   *         UserInfo emplenant com a mínim username i nif.
   */
  public UserInfo getUserInfoByUserName(String username) throws Exception;

  boolean authenticate(String username, String password) throws Exception;

  boolean authenticate(X509Certificate certificate) throws Exception;

  String[] getAllUsernames() throws Exception;

  /**
   * Mètode que retorna els roles associats a l'usuari username per
   * l'aplicatiu
   * 
   * @param username
   * @return
   */
  public RolesInfo getRolesByUsername(String username) throws Exception;

  /**
   * Retorna els usernames dels usuris que tenen rol <param>rol</param>
   * @return Una llista, buida
   *         o no, dels usuaris que tenen aquest rol.
   * @throws Exception Si aquesta operació no esta disponible
   */
  public String[] getUsernamesByRol(String rol) throws Exception;

}