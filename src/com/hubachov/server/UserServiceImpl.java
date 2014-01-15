package com.hubachov.server;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.BaseListLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.FilterConfig;
import com.extjs.gxt.ui.client.data.FilterPagingLoadConfig;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hubachov.client.model.User;
import com.hubachov.client.service.UserService;
import com.hubachov.dao.RoleDAO;
import com.hubachov.dao.UserDAO;
import com.hubachov.dao.impl.jdbc.RoleDAOJDBC;
import com.hubachov.dao.impl.jdbc.UserDAOJDBC;
import org.apache.log4j.Logger;

import java.util.*;

public class UserServiceImpl extends RemoteServiceServlet implements UserService {
    private static final Logger log = Logger.getLogger(UserServiceImpl.class);
    private UserDAO dao = new UserDAOJDBC();
    private RoleDAO roleDAO = new RoleDAOJDBC();

    @Override
    public BasePagingLoadResult<User> getUsers(FilterPagingLoadConfig config) throws Exception {
        List<User> users = dao.findAll();
        enrich(users);
        int total = users.size();
        sortUsers(config, users);
        filter(config, users);
        reduce(config, users);
        return new BasePagingLoadResult<User>(users, config.getOffset(), total);
    }

    private void enrich(List<User> users) throws Exception {
        for (User user : users) {
            roleDAO.enrichUser(user);
        }
    }

    private void filter(FilterPagingLoadConfig config, List<User> sorted) {
        List<FilterConfig> list = config.getFilterConfigs();
        if (list != null && !list.isEmpty()) {
            for (FilterConfig filter : list) {
                if (filter.getType().equals("numeric")) {
                    filterByNumber(filter, sorted);
                    continue;
                }
                if (filter.getType().equals("string")) {
                    filterByString(filter, sorted);
                    continue;
                }
                if (filter.getType().equals("date")) {
                    filterByDate(filter, sorted);
                    continue;
                }
                if (filter.getType().equals("list")) {
                    filterByList(filter, sorted);
                    continue;
                }
            }
        }
    }

    private void filterByList(FilterConfig filter, List<User> sorted) {
        String field = filter.getField();
        List<String> restrictions = ((ArrayList<String>) filter.getValue());
        Iterator<User> iterator = sorted.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            String userValue = user.get(field);
            boolean coincidence = false;
            for (String restriction : restrictions) {
                if (restriction.equals(userValue)) {
                    coincidence = true;
                    break;
                }
            }
            if (!coincidence) {
                iterator.remove();
            }
        }
    }

    private void filterByDate(FilterConfig filter, List<User> sorted) {
        String field = filter.getField();
        Date value = (Date) filter.getValue();
        String comparison = filter.getComparison();
        Iterator<User> iterator = sorted.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            Calendar date = Calendar.getInstance();
            Calendar point = Calendar.getInstance();
            point.setTime(value);
            date.setTime((Date) user.get(field));
            if (comparison.equals("on")) {
                if (point.get(Calendar.YEAR) == date.get(Calendar.YEAR)) {
                    if (point.get(Calendar.DAY_OF_YEAR) != date.get(Calendar.DAY_OF_YEAR)) {
                        iterator.remove();
                    }
                } else if (point.get(Calendar.YEAR) != date.get(Calendar.YEAR)) {
                    iterator.remove();
                }
                continue;
            }
            if (comparison.equals("before")) {
                if (point.get(Calendar.YEAR) < date.get(Calendar.YEAR)) {
                    iterator.remove();
                } else if (point.get(Calendar.YEAR) == date.get(Calendar.YEAR)) {
                    if (point.get(Calendar.DAY_OF_YEAR) <= date.get(Calendar.DAY_OF_YEAR)) {
                        iterator.remove();
                    }
                }
                continue;
            }
            if (comparison.equals("after")) {
                if (point.get(Calendar.YEAR) > date.get(Calendar.YEAR)) {
                    iterator.remove();
                } else if (point.get(Calendar.DAY_OF_YEAR) >= date.get(Calendar.DAY_OF_YEAR)) {
                    iterator.remove();
                }
            }
        }
    }

    private void filterByString(FilterConfig filter, List<User> sorted) {
        String field = filter.getField();
        String pattern = (String) filter.getValue();
        Iterator<User> iterator = sorted.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (!((String) user.get(field)).contains(pattern)) {
                iterator.remove();
            }
        }
    }

    private void filterByNumber(FilterConfig filter, List<User> sorted) {
        String field = filter.getField();
        String comparison = filter.getComparison();
        long value = Math.round((double) filter.getValue());
        Iterator<User> iterator = sorted.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (comparison.equals("lt") && (Long) user.get(field) > value) {
                iterator.remove();
                continue;
            }
            if (comparison.equals("gt") && (Long) user.get(field) < value) {
                iterator.remove();
                continue;
            }
            if (comparison.equals("eq") && (Long) user.get(field) != value) {
                iterator.remove();
                continue;
            }
        }
    }

    @Override
    public void update(User user) throws Exception {
        try {
            user.setRole(roleDAO.findByName(user.get("role").toString()));
            dao.update(user);
        } catch (Exception e) {
            log.error("Can't update user#" + user.getId(), e);
            throw e;
        }
    }

    @Override
    public void remove(User user) throws Exception {
        try {
            dao.remove(user);
        } catch (Exception e) {
            log.error("Can't remove user#" + user.getId(), e);
            throw e;
        }
    }

    @Override
    public void create(User user) throws Exception {
        try {
            user.setRole(roleDAO.findByName(user.get("role").toString()));
            dao.create(user);
        } catch (Exception e) {
            log.error("Can't create user#" + user.getLogin(), e);
            throw e;
        }
    }

    @Override
    public boolean checkLogin(String login) throws Exception {
        try {
            return dao.findByLogin(login) == null;
        } catch (Exception e) {
            log.error("Can't check login " + login, e);
            throw e;
        }
    }

    @Override
    public BasePagingLoadResult<User> loadUsers(BaseListLoadConfig loadConfig) throws Exception {
        return new BasePagingLoadResult<User>(dao.findAll());
    }

    private void sortUsers(FilterPagingLoadConfig config, List<User> users) {
        final Style.SortDir direction = config.getSortDir();
        if (config.getSortInfo().getSortField() != null) {
            final String sortField = config.getSortInfo().getSortField();
            Collections.sort(users, new Comparator<User>() {
                @Override
                public int compare(User first, User second) {
                    if (direction.equals(Style.SortDir.DESC)) {
                        User tmp = first;
                        first = second;
                        second = tmp;
                    }
                    if (sortField.equals("login")) {
                        return first.getLogin().compareTo(second.getLogin());
                    }
                    if (sortField.equals("email")) {
                        return first.getEmail().compareTo(second.getEmail());
                    }
                    if (sortField.equals("firstName")) {
                        return first.getFirstName().compareTo(second.getFirstName());
                    }
                    if (sortField.equals("lastName")) {
                        return first.getLastName().compareTo(second.getLastName());
                    }
                    if (sortField.equals("birthday")) {
                        return first.getBirthday().compareTo(second.getBirthday());
                    }
                    if (sortField.equals("role")) {
                        return first.getRole().getName().compareTo(second.getRole().getName());
                    }
                    return (int) (first.getId() - second.getId());
                }
            });
        }
    }

    private void reduce(FilterPagingLoadConfig config, List<User> users) {
        int start = config.getOffset();
        int limit = users.size();
        if (config.getLimit() > 0) {
            limit = Math.min(start + config.getLimit(), limit);
        }
        List<User> result = new ArrayList<User>();
        for (int i = start; i < limit; i++) {
            result.add(users.get(i));
        }
        users.clear();
        users.addAll(result);
    }
}